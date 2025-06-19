package meow_be.eventposts.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import meow_be.eventposts.dto.EventPostRankingDto;
import meow_be.eventposts.domain.QEventPost;
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
}
