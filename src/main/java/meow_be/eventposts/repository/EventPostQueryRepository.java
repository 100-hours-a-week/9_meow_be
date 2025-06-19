package meow_be.eventposts.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import meow_be.eventposts.domain.QEventWeek;
import meow_be.eventposts.dto.EventPostRankingDto;
import meow_be.eventposts.domain.QEventPost;
import meow_be.eventposts.dto.EventTopRankDto;
import meow_be.users.domain.QUser;
import org.springframework.stereotype.Repository;

import java.util.List;

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
    public List<EventTopRankDto> findTop3RankedPostsByWeek() {
        QEventPost post = QEventPost.eventPost;
        QUser user = QUser.user;
        QEventWeek week = QEventWeek.eventWeek;

        return queryFactory
                .select(Projections.constructor(EventTopRankDto.class,
                        week.week,
                        post.id,
                        post.imageUrl,
                        user.nickname,
                        user.profileImageUrl,
                        user.animalType,
                        post.likeCount,
                        post.ranking
                ))
                .from(post)
                .join(post.user, user)
                .join(post.eventWeek, week)
                .where(post.ranking.between(1, 3))
                .orderBy(week.week.asc(), post.ranking.asc())
                .fetch();
    }
}
