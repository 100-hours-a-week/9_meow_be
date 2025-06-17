package meow_be.posts.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.jpa.JPAExpressions;
import lombok.RequiredArgsConstructor;
import meow_be.posts.domain.QComment;
import meow_be.posts.domain.QPost;
import meow_be.posts.domain.QPostImage;
import meow_be.posts.domain.QPostLike;
import meow_be.posts.dto.PostDto;
import meow_be.posts.dto.PostEditInfoDto;
import meow_be.users.domain.QFollow;
import meow_be.users.domain.QUser;
import meow_be.posts.dto.PostSummaryDto;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

import static meow_be.posts.domain.QPostImage.postImage;

@Repository
@RequiredArgsConstructor
public class PostQueryRepositoryImpl implements PostQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<PostSummaryDto> findPostsByPostType(String postType, Pageable pageable, Integer userId) {
        QPost post = QPost.post;
        QUser user = QUser.user;
        QPostImage postImage = QPostImage.postImage;
        QPostLike postLike = QPostLike.postLike;
        QComment comment = QComment.comment;
        QFollow follow = QFollow.follow;



        var query = queryFactory
                        .select(Projections.constructor(PostSummaryDto.class,
                                post.id,
                                user.id,
                                user.nickname,
                                user.profileImageUrl,
                                post.transformedContent,
                                post.emotion,
                                post.postType,
                                JPAExpressions.select(postImage.imageUrl)
                                        .from(postImage)
                                        .where(postImage.post.id.eq(post.id)
                                                .and(postImage.imageNumber.eq(0)))
                                        .limit(1),
                                JPAExpressions.select(comment.count())
                                        .from(comment)
                                        .where(comment.post.id.eq(post.id)
                                                .and(comment.isDeleted.isFalse())),
                                JPAExpressions.select(postLike.count())
                                        .from(postLike)
                                        .where(postLike.post.id.eq(post.id)
                                                .and(postLike.isLiked.isTrue())),
                                userId != null ?
                                        JPAExpressions.selectOne()
                                                .from(postLike)
                                                .where(postLike.post.id.eq(post.id)
                                                        .and(postLike.user.id.eq(userId))
                                                        .and(postLike.isLiked.isTrue()))
                                                .exists()
                                        : Expressions.constant(false),
                                userId != null ?
                                        post.user.id.eq(userId)
                                        : Expressions.constant(false),
                                post.createdAt,
                                post.updatedAt,
                                userId != null ?
                                        JPAExpressions.selectOne()
                                                .from(follow)
                                                .where(
                                                        follow.follower.id.eq(userId)
                                                                .and(follow.following.id.eq(post.user.id))
                                                                .and(follow.isDeleted.isFalse())
                                                )
                                                .exists()
                                        : Expressions.constant(false)


                        ))
                .from(post)
                .join(post.user, user)
                .where(post.isDeleted.isFalse());

        if (postType != null && !postType.isBlank()) {
            query.where(post.postType.eq(postType));
        }

        long total = query.fetchCount();

        List<PostSummaryDto> content = query
                .orderBy(post.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(content, pageable, total);
    }
    @Override
    public PostDto findPostDetailById(int postId, Integer userId) {
        QPost post = QPost.post;
        QUser user = QUser.user;
        QPostLike postLike = QPostLike.postLike;
        QComment comment = QComment.comment;
        QPostImage postImage = QPostImage.postImage;
        QFollow follow = QFollow.follow;

        PostDto basePostDto = queryFactory
                .select(Projections.constructor(PostDto.class,
                        post.id,
                        user.id,
                        user.nickname,
                        user.profileImageUrl,
                        post.transformedContent,
                        post.emotion,
                        post.postType,
                        Expressions.constant(Collections.emptyList()),
                        JPAExpressions.select(comment.count())
                                .from(comment)
                                .where(comment.post.id.eq(post.id)
                                        .and(comment.isDeleted.isFalse())),
                        JPAExpressions.select(postLike.count())
                                .from(postLike)
                                .where(postLike.post.id.eq(post.id)
                                        .and(postLike.isLiked.isTrue())),
                        userId != null ?
                                JPAExpressions.selectOne()
                                        .from(postLike)
                                        .where(postLike.post.id.eq(post.id)
                                                .and(postLike.user.id.eq(userId))
                                                .and(postLike.isLiked.isTrue()))
                                        .exists()
                                : Expressions.constant(false),
                        userId != null ?
                                post.user.id.eq(userId)
                                : Expressions.constant(false),
                        userId != null ?
                                Expressions.cases()
                                        .when(
                                                JPAExpressions.selectOne()
                                                        .from(follow)
                                                        .where(follow.follower.id.eq(userId)
                                                                .and(follow.following.id.eq(post.user.id))
                                                                .and(follow.isDeleted.isFalse()))
                                                        .exists()
                                        )
                                        .then(true)
                                        .otherwise(false)
                                : Expressions.constant(false),
                        post.createdAt,
                        post.updatedAt
                ))
                .from(post)
                .join(post.user, user)
                .where(post.id.eq(postId)
                        .and(post.isDeleted.isFalse()))
                .fetchOne();

        if (basePostDto == null) {
            throw new IllegalArgumentException("게시물을 찾을 수 없습니다.");
        }

        List<String> imageUrls = queryFactory
                .select(postImage.imageUrl)
                .from(postImage)
                .where(postImage.post.id.eq(postId))
                .orderBy(postImage.imageNumber.asc())
                .fetch();

        return PostDto.builder()
                .id(basePostDto.getId())
                .userId(basePostDto.getUserId())
                .nickname(basePostDto.getNickname())
                .profileImageUrl(basePostDto.getProfileImageUrl())
                .transformedContent(basePostDto.getTransformedContent())
                .emotion(basePostDto.getEmotion())
                .postType(basePostDto.getPostType())
                .imageUrls(imageUrls)
                .commentCount(basePostDto.getCommentCount())
                .likeCount(basePostDto.getLikeCount())
                .isLiked(basePostDto.isLiked())
                .isMyPost(basePostDto.isMyPost())
                .isFollowing(basePostDto.isFollowing())
                .createdAt(basePostDto.getCreatedAt())
                .updatedAt(basePostDto.getUpdatedAt())
                .build();
    }


    public Page<PostSummaryDto> findUserPostSummaryPage(Integer targetUserId, Integer loginUserId, boolean isOwner, Pageable pageable) {
        QPost post = QPost.post;
        QUser user = QUser.user;
        QPostImage postImage = QPostImage.postImage;
        QPostLike postLike = QPostLike.postLike;
        QComment comment = QComment.comment;
        QFollow follow = QFollow.follow;

        var query = queryFactory
                .select(Projections.constructor(PostSummaryDto.class,
                        post.id,
                        user.id,
                        user.nickname,
                        user.profileImageUrl,
                        post.transformedContent,
                        post.emotion,
                        post.postType,
                        JPAExpressions.select(postImage.imageUrl)
                                .from(postImage)
                                .where(postImage.post.id.eq(post.id)
                                        .and(postImage.imageNumber.eq(0)))
                                .limit(1),
                        JPAExpressions.select(comment.count())
                                .from(comment)
                                .where(comment.post.id.eq(post.id)
                                        .and(comment.isDeleted.isFalse())),
                        JPAExpressions.select(postLike.count())
                                .from(postLike)
                                .where(postLike.post.id.eq(post.id)
                                        .and(postLike.isLiked.isTrue())),
                        loginUserId != null ?
                                JPAExpressions.selectOne()
                                        .from(postLike)
                                        .where(postLike.post.id.eq(post.id)
                                                .and(postLike.user.id.eq(loginUserId))
                                                .and(postLike.isLiked.isTrue()))
                                        .exists()
                                : Expressions.constant(false),
                        loginUserId != null ?
                                Expressions.cases()
                                        .when(post.user.id.eq(loginUserId))
                                        .then(true)
                                        .otherwise(false)
                                : Expressions.constant(false),
                        post.createdAt,
                        post.updatedAt,
                        loginUserId != null ?
                                JPAExpressions.selectOne()
                                        .from(follow)
                                        .where(
                                                follow.follower.id.eq(loginUserId)
                                                        .and(follow.following.id.eq(post.user.id))
                                                        .and(follow.isDeleted.isFalse())
                                        )
                                        .exists()
                                : Expressions.constant(false)
                ))
                .from(post)
                .join(post.user, user)
                .where(post.isDeleted.isFalse()
                        .and(post.user.id.eq(targetUserId)));

        long total = query.fetchCount();

        List<PostSummaryDto> content = query
                .orderBy(post.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(content, pageable, total);
    }



    @Override
    public PostEditInfoDto findPostEditInfoById(Integer postId) {
        QPost post = QPost.post;
        QUser user = QUser.user;
        QPostImage postImage = QPostImage.postImage;

        PostEditInfoDto editInfo = queryFactory
                .select(Projections.constructor(PostEditInfoDto.class,
                        post.id,
                        user.nickname,
                        user.profileImageUrl,
                        post.content,
                        post.emotion,
                        Expressions.constant(Collections.emptyList())
                ))
                .from(post)
                .join(post.user, user)
                .where(post.id.eq(postId)
                        .and(post.isDeleted.isFalse()))
                .fetchOne();

        List<String> imageUrls = queryFactory
                .select(postImage.imageUrl)
                .from(postImage)
                .where(postImage.post.id.eq(postId))
                .orderBy(postImage.imageNumber.asc())
                .fetch();

        return new PostEditInfoDto(
                editInfo.getId(),
                editInfo.getUsername(),
                editInfo.getProfileImageUrl(),
                editInfo.getContent(),
                editInfo.getEmotion(),
                imageUrls
        );
    }

    @Override
    public Page<PostSummaryDto> findFollowingPosts(Integer userId, Pageable pageable) {
        QPost post = QPost.post;
        QUser user = QUser.user;
        QFollow follow = QFollow.follow;
        QPostLike postLike = QPostLike.postLike;
        QComment comment = QComment.comment;

        JPQLQuery<Integer> followedUserIds = JPAExpressions
                .select(follow.following.id)
                .from(follow)
                .where(follow.follower.id.eq(userId));

        var query = queryFactory
                .select(Projections.constructor(PostSummaryDto.class,
                        post.id,
                        user.id,
                        user.nickname,
                        user.profileImageUrl,
                        post.transformedContent,
                        post.emotion,
                        post.postType,
                        JPAExpressions.select(postImage.imageUrl)
                                .from(postImage)
                                .where(postImage.post.id.eq(post.id)
                                        .and(postImage.imageNumber.eq(0)))
                                .limit(1),
                        JPAExpressions.select(comment.count())
                                .from(comment)
                                .where(comment.post.id.eq(post.id)
                                        .and(comment.isDeleted.isFalse())),
                        JPAExpressions.select(postLike.count())
                                .from(postLike)
                                .where(postLike.post.id.eq(post.id)
                                        .and(postLike.isLiked.isTrue())),
                        userId != null ?
                                JPAExpressions.selectOne()
                                        .from(postLike)
                                        .where(postLike.post.id.eq(post.id)
                                                .and(postLike.user.id.eq(userId))
                                                .and(postLike.isLiked.isTrue()))
                                        .exists()
                                : Expressions.constant(false),
                        userId != null ?
                                post.user.id.eq(userId)
                                : Expressions.constant(false),
                        userId != null ?
                                JPAExpressions.selectOne()
                                        .from(follow)
                                        .where(follow.follower.id.eq(userId)
                                                .and(follow.following.id.eq(post.user.id)))
                                        .exists()
                                : Expressions.constant(false),
                        post.createdAt,
                        post.updatedAt
                ))
                .from(post)
                .join(post.user, user)
                .leftJoin(postLike)
                .on(postLike.post.id.eq(post.id)
                        .and(postLike.user.id.eq(userId))
                        .and(postLike.isLiked.eq(true)))
                .where(user.id.in(followedUserIds))
                .orderBy(post.createdAt.desc());

        long total = query.fetchCount();

        List<PostSummaryDto> content = query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(content, pageable, total);
    }
}
