package meow_be.users.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import meow_be.users.dto.EditUserProfileResponse;
import meow_be.users.dto.MyProfileResponse;
import meow_be.users.dto.UserProfileResponse;
import meow_be.users.domain.User;
import org.springframework.stereotype.Repository;

import static meow_be.posts.domain.QPost.post;
import static meow_be.users.domain.QFollow.follow;
import static meow_be.users.domain.QUser.user;

@Repository
@RequiredArgsConstructor
public class UserQueryRepositoryImpl implements UserQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public UserProfileResponse findUserProfile(Integer targetUserId, Integer loginUserId) {

        User userInfo = queryFactory
                .selectFrom(user)
                .where(user.id.eq(targetUserId)
                        .and(user.isDeleted.isFalse()))
                .fetchOne();

        if (userInfo == null) {
            return null;
        }

        long postCount = queryFactory
                .select(post.count())
                .from(post)
                .where(post.user.id.eq(targetUserId)
                        .and(post.isDeleted.isFalse()))
                .fetchOne();

        long followerCount = queryFactory
                .select(follow.count())
                .from(follow)
                .where(follow.following.id.eq(targetUserId)
                        .and(follow.isDeleted.isFalse()))
                .fetchOne();

        long followingCount = queryFactory
                .select(follow.count())
                .from(follow)
                .where(follow.follower.id.eq(targetUserId)
                        .and(follow.isDeleted.isFalse()))
                .fetchOne();

        boolean isFollowing = loginUserId != null &&
                queryFactory
                        .selectOne()
                        .from(follow)
                        .where(follow.follower.id.eq(loginUserId)
                                .and(follow.following.id.eq(targetUserId))
                                .and(follow.isDeleted.isFalse()))
                        .fetchFirst() != null;

        boolean isUser = loginUserId != null && loginUserId.equals(targetUserId);

        return new UserProfileResponse(
                userInfo.getNickname(),
                userInfo.getAnimalType(),
                userInfo.getProfileImageUrl(),
                postCount,
                followerCount,
                followingCount,
                isFollowing,
                isUser
        );
    }

    @Override
    public EditUserProfileResponse findEditUserProfile(Integer userId) {
        User userInfo = queryFactory
                .selectFrom(user)
                .where(user.id.eq(userId)
                        .and(user.isDeleted.isFalse()))
                .fetchOne();

        if (userInfo == null) {
            return null;
        }

        return new EditUserProfileResponse(
                userInfo.getNickname(),
                userInfo.getProfileImageUrl()
        );
    }

    @Override
    public MyProfileResponse findMyProfile(Integer userId) {
        User userInfo = queryFactory
                .selectFrom(user)
                .where(user.id.eq(userId)
                        .and(user.isDeleted.isFalse()))
                .fetchOne();

        if (userInfo == null) {
            return null;
        }

        long postCount = queryFactory
                .select(post.count())
                .from(post)
                .where(post.user.id.eq(userId)
                        .and(post.isDeleted.isFalse()))
                .fetchOne();

        long followerCount = queryFactory
                .select(follow.count())
                .from(follow)
                .where(follow.following.id.eq(userId))
                .fetchOne();

        long followingCount = queryFactory
                .select(follow.count())
                .from(follow)
                .where(follow.follower.id.eq(userId))
                .fetchOne();

        return MyProfileResponse.builder()
                .userId(userId)
                .nickname(userInfo.getNickname())
                .animalType(userInfo.getAnimalType())
                .profileImageUrl(userInfo.getProfileImageUrl())
                .postCount(postCount)
                .followerCount(followerCount)
                .followingCount(followingCount)
                .build();
    }


}
