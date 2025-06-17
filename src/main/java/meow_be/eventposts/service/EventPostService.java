package meow_be.eventposts.service;

import com.amazonaws.services.kms.model.NotFoundException;
import lombok.RequiredArgsConstructor;
import meow_be.eventposts.domain.EventPost;
import meow_be.eventposts.domain.EventWeek;
import meow_be.eventposts.repository.EventPostRepository;
import meow_be.eventposts.repository.EventWeekRepository;
import meow_be.users.domain.User;
import meow_be.users.repository.UserRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class EventPostService {
    private final RedisTemplate<String, String> redisTemplate;
    private final EventPostRepository eventPostRepository;
    private final UserRepository userRepository;
    private final EventWeekRepository eventWeekRepository;
    public void saveWeeklyRanking(int week) {
        String redisKey = "event:likes:week:" + week;
        ZSetOperations<String, String> zSetOps = redisTemplate.opsForZSet();
        Set<ZSetOperations.TypedTuple<String>> ranking = zSetOps.reverseRangeWithScores(redisKey, 0, -1);

        if (ranking != null) {
            int rank = 1;
            for (ZSetOperations.TypedTuple<String> tuple : ranking) {
                Integer postId = Integer.parseInt(tuple.getValue());
                int likeCount = (int) tuple.getScore().doubleValue();

                int currentRank = rank++;
                eventPostRepository.findById(postId).ifPresent(post -> {
                    EventPost updated = post.toBuilder()
                            .likeCount(likeCount)
                            .ranking(currentRank)
                            .build();
                    eventPostRepository.save(updated);
                });
            }
        }
    }

    public void likeEventPost(Integer postId) {
        EventPost post = eventPostRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("이벤트 게시물을 찾을 수 없습니다."));

        int week = post.getEventWeek().getWeek();
        String key = "event:likes:week:" + week;

        redisTemplate.opsForZSet().incrementScore(key, postId.toString(), 1);
    }
    public Integer createEventPost(Integer userId, String imageUrl) {
        int currentWeek = getCurrentWeek();

        User user = userRepository.findByIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new NotFoundException("유저를 찾을 수 없습니다."));

        EventWeek eventWeek = eventWeekRepository.findById(currentWeek)
                .orElseThrow(() -> new NotFoundException("이벤트 주차 정보를 찾을 수 없습니다."));

        EventPost eventPost = EventPost.builder()
                .user(user)
                .eventWeek(eventWeek)
                .imageUrl(imageUrl)
                .ranking(0)
                .likeCount(0)
                .createdAt(LocalDateTime.now())
                .build();

        return eventPostRepository.save(eventPost).getId();
    }



    private int getCurrentWeek() {
        LocalDate start = LocalDate.of(2025, 6, 16);
        LocalDate now = LocalDate.now();
        return (int) ChronoUnit.WEEKS.between(start, now) + 1;
    }
}

