package meow_be.users.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.core.types.Projections;
import lombok.RequiredArgsConstructor;
import meow_be.users.domain.QFollow;
import meow_be.users.domain.QUser;
import meow_be.users.dto.FollowUserDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class FollowQueryRepositoryImpl implements FollowQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<FollowUserDto> findFollowingsByUserId(Integer userId, Pageable pageable) {
        QFollow follow = QFollow.follow;
        QUser user = QUser.user;

        // 콘텐츠 조회 쿼리
        List<FollowUserDto> content = queryFactory
                .select(Projections.constructor(FollowUserDto.class,
                        user.id,
                        user.nickname,
                        user.animalType,
                        user.profileImageUrl
                ))
                .from(follow)
                .join(follow.following, user)
                .where(follow.follower.id.eq(userId)
                        .and(follow.isDeleted.isFalse())
                        .and(user.isDeleted.isFalse()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory
                .select(user.count())
                .from(follow)
                .join(follow.following, user)
                .where(follow.follower.id.eq(userId)
                        .and(follow.isDeleted.isFalse())
                        .and(user.isDeleted.isFalse()))
                .fetchOne();

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Page<FollowUserDto> findFollowersByUserId(Integer userId, Pageable pageable) {
        QFollow follow = QFollow.follow;
        QUser user = QUser.user;

        List<FollowUserDto> content = queryFactory
                .select(Projections.constructor(FollowUserDto.class,
                        user.id,
                        user.nickname,
                        user.animalType,
                        user.profileImageUrl
                ))
                .from(follow)
                .join(follow.follower, user)
                .where(follow.following.id.eq(userId)
                        .and(follow.isDeleted.isFalse())
                        .and(user.isDeleted.isFalse()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory
                .select(user.count())
                .from(follow)
                .join(follow.follower, user)
                .where(follow.following.id.eq(userId)
                        .and(follow.isDeleted.isFalse())
                        .and(user.isDeleted.isFalse()))
                .fetchOne();

        return new PageImpl<>(content, pageable, total);
    }

}
