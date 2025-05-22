package meow_be.posts.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.jpa.JPAExpressions;
import lombok.RequiredArgsConstructor;
import meow_be.posts.domain.QPost;
import meow_be.posts.domain.QPostImage;
import meow_be.posts.domain.QPostLike;
import meow_be.users.domain.QUser;
import meow_be.posts.dto.PostSummaryDto;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;

import java.util.List;

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
                                post.commentCount,
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
                                post.updatedAt

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
    public Page<PostSummaryDto> findUserPostSummaryPage(Integer userId, Pageable pageable) {
        QPost post = QPost.post;
        QUser user = QUser.user;
        QPostImage postImage = QPostImage.postImage;
        QPostLike postLike = QPostLike.postLike;

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
                        post.commentCount,
                        JPAExpressions.select(postLike.count())
                                .from(postLike)
                                .where(postLike.post.id.eq(post.id)
                                        .and(postLike.isLiked.isTrue())),
                        JPAExpressions.selectOne()
                                .from(postLike)
                                .where(postLike.post.id.eq(post.id)
                                        .and(postLike.user.id.eq(userId))
                                        .and(postLike.isLiked.isTrue()))
                                .exists(),
                        post.user.id.eq(userId),
                        post.createdAt,
                        post.updatedAt
                ))
                .from(post)
                .join(post.user, user)
                .where(post.isDeleted.isFalse()
                        .and(post.user.id.eq(userId)));

        long total = query.fetchCount();

        List<PostSummaryDto> content = query
                .orderBy(post.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(content, pageable, total);
    }

}
