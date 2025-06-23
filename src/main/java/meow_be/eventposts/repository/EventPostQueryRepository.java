package meow_be.eventposts.repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import meow_be.eventposts.domain.QEventWeek;
import meow_be.eventposts.dto.EventImageRankDto;
import meow_be.eventposts.dto.EventPostRankingDto;
import meow_be.eventposts.domain.QEventPost;;
import meow_be.users.domain.QUser;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;

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
    public List<EventImageRankDto> findTop3RankedPostsGroupedByWeek() {
        QEventPost post = QEventPost.eventPost;
        QUser user = QUser.user;
        QEventWeek week = QEventWeek.eventWeek;

        List<Tuple> tuples = queryFactory
                .select(
                        week.week,
                        week.topic,
                        week.endVoteAt,
                        post.imageUrl,
                        post.ranking
                )
                .from(post)
                .join(post.user, user)
                .join(post.eventWeek, week)
                .where(post.ranking.between(1, 3))
                .orderBy(week.week.asc(), post.ranking.asc())
                .fetch();

        Map<Integer, List<String>> imageUrlMap = new LinkedHashMap<>();
        Map<Integer, String> topicMap = new HashMap<>();
        Map<Integer, LocalDateTime> endAtMap = new HashMap<>();

        for (Tuple tuple : tuples) {
            Integer weekNum = tuple.get(week.week);
            String imageUrl = tuple.get(post.imageUrl);
            imageUrlMap.computeIfAbsent(weekNum, k -> new ArrayList<>()).add(imageUrl);
            topicMap.put(weekNum, tuple.get(week.topic));
            endAtMap.put(weekNum, tuple.get(week.endVoteAt));
        }

        List<EventImageRankDto> result = new ArrayList<>();
        for (Integer weekNum : imageUrlMap.keySet()) {
            result.add(new EventImageRankDto(
                    weekNum,
                    topicMap.get(weekNum),
                    endAtMap.get(weekNum),
                    imageUrlMap.get(weekNum)
            ));
        }

        return result;
    }


}
