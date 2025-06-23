package meow_be.eventposts.repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import meow_be.eventposts.domain.QEventWeek;
import meow_be.eventposts.dto.EventPostRankingDto;
import meow_be.eventposts.domain.QEventPost;
import meow_be.eventposts.dto.EventTopRankDto;
import meow_be.eventposts.dto.EventWeekRankDto;
import meow_be.users.domain.QUser;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;


import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class EventPostQueryRepository {

    private final JPAQueryFactory queryFactory;

    public List<EventPostRankingDto> findRankedPostsByWeek(int week) {
        QEventPost post = QEventPost.eventPost;
        QUser user = QUser.user;

        return queryFactory
                .select(Projections.constructor(EventPostRankingDto.class,
                        post.id,
                        post.imageUrl,
                        user.nickname,
                        user.animalType,
                        user.profileImageUrl,
                        user.id,
                        post.likeCount
                ))
                .from(post)
                .join(post.user, user)
                .where(post.eventWeek.week.eq(week))
                .orderBy(post.ranking.asc())
                .fetch();
    }
    public List<EventWeekRankDto> findTop3RankedPostsGroupedByWeek() {
        QEventPost post = QEventPost.eventPost;
        QUser user = QUser.user;
        QEventWeek week = QEventWeek.eventWeek;
        
        List<Tuple> tuples = queryFactory
                .select(
                        week.week,
                        week.topic,
                        week.endVoteAt,
                        post.id,
                        post.imageUrl,
                        user.nickname,
                        user.profileImageUrl,
                        user.animalType,
                        post.likeCount,
                        post.ranking
                )
                .from(post)
                .join(post.user, user)
                .join(post.eventWeek, week)
                .where(post.ranking.between(1, 3))
                .orderBy(week.week.asc(), post.ranking.asc())
                .fetch();

        // 주차별로 그룹핑
        Map<Integer, List<EventTopRankDto>> rankMap = new LinkedHashMap<>();
        Map<Integer, String> topicMap = new HashMap<>();
        Map<Integer, LocalDateTime> endAtMap = new HashMap<>();

        for (Tuple tuple : tuples) {
            Integer weekNum = tuple.get(week.week);
            rankMap.computeIfAbsent(weekNum, k -> new ArrayList<>())
                    .add(new EventTopRankDto(
                            weekNum,
                            tuple.get(post.id),
                            tuple.get(post.imageUrl),
                            tuple.get(user.nickname),
                            tuple.get(user.profileImageUrl),
                            tuple.get(user.animalType),
                            tuple.get(post.likeCount),
                            tuple.get(post.ranking)
                    ));
            topicMap.put(weekNum, tuple.get(week.topic));
            endAtMap.put(weekNum, tuple.get(week.endVoteAt));
        }
        List<EventWeekRankDto> result = new ArrayList<>();
        for (Integer weekNum : rankMap.keySet()) {
            result.add(new EventWeekRankDto(
                    weekNum,
                    topicMap.get(weekNum),
                    endAtMap.get(weekNum),
                    rankMap.get(weekNum)
            ));
        }

        return result;
    }


}
