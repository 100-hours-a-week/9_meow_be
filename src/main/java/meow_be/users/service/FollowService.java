package meow_be.users.service;

import jakarta.transaction.Transactional;
import meow_be.users.domain.Follow;
import meow_be.users.domain.User;
import meow_be.users.repository.FollowRepository;
import meow_be.users.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    public FollowService(FollowRepository followRepository, UserRepository userRepository) {
        this.followRepository = followRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void followUser(Integer followerId, Integer followingId) {
        if (followerId.equals(followingId)) {
            throw new IllegalArgumentException("자기 자신을 팔로우할 수 없습니다.");
        }

        User follower = userRepository.findById(followerId)
                .orElseThrow(() -> new IllegalArgumentException("팔로우하는 사용자 없음"));

        User following = userRepository.findById(followingId)
                .orElseThrow(() -> new IllegalArgumentException("팔로우 당하는 사용자 없음"));

        Optional<Follow> existingFollowOpt = followRepository.findByFollowerAndFollowing(follower, following);

        if (existingFollowOpt.isPresent()) {
            Follow existingFollow = existingFollowOpt.get();
            if (existingFollow.isActive()) {
                return;
            } else {
                existingFollow.restore();
                followRepository.save(existingFollow);
                return;
            }
        }

        Follow follow = new Follow(follower, following);
        followRepository.save(follow);
    }

    @Transactional
    public void unfollowUser(Integer followerId, Integer followingId) {
        User follower = userRepository.findById(followerId)
                .orElseThrow(() -> new IllegalArgumentException("팔로우하는 사용자 없음"));

        User following = userRepository.findById(followingId)
                .orElseThrow(() -> new IllegalArgumentException("팔로우 당하는 사용자 없음"));

        Follow follow = followRepository.findByFollowerAndFollowing(follower, following)
                .orElseThrow(() -> new IllegalArgumentException("팔로우 관계가 존재하지 않습니다."));

        if (follow.isActive()) {
            follow.markAsDeleted();
            followRepository.save(follow);
        }
    }
}
