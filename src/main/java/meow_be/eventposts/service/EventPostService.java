package meow_be.eventposts.service;

import com.amazonaws.services.kms.model.NotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;

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
                .createdAt(LocalDateTime.now(ZoneId.of("Asia/Seoul")))
                .build();

        EventPost savedPost = eventPostRepository.save(eventPost);
        Integer postId = savedPost.getId();

        String redisKey = "event:likes:week:" + currentWeek;
        redisTemplate.opsForZSet().add(redisKey, String.valueOf(postId), 0);

        cachePostWithUserInfo(savedPost);

        return postId;
    }

    public boolean hasAppliedToCurrentWeek(Integer userId) {
        int currentWeek = getCurrentWeek();
        EventWeek eventWeek = eventWeekRepository.findById(currentWeek)
                .orElseThrow(() -> new NotFoundException("이벤트 주차 정보를 찾을 수 없습니다."));

        return eventPostRepository.existsByUserIdAndEventWeek(userId, eventWeek);
    }
    public List<Map<String, Object>> getTop3Ranking() {
        int currentWeek = getCurrentWeek();
        String key = "event:likes:week:" + currentWeek;

        Set<ZSetOperations.TypedTuple<String>> top3 = redisTemplate.opsForZSet()
                .reverseRangeWithScores(key, 0, 2);

        if (top3 == null || top3.isEmpty()) {
            return List.of();
        }

        List<Map<String, Object>> result = new ArrayList<>();
        int ranking = 1;

        for (ZSetOperations.TypedTuple<String> tuple : top3) {
            Integer postId = Integer.parseInt(tuple.getValue());
            int likeCount = (int) tuple.getScore().doubleValue();

            Map<String, Object> entry = new HashMap<>();
            entry.put("postId", postId);
            entry.put("ranking", ranking++);
            entry.put("likeCount", likeCount);
            result.add(entry);
        }

        return result;
    }

    public List<Map<String, Object>> getPostLikeSummary() {
        int currentWeek = getCurrentWeek();
        String key = "event:likes:week:" + currentWeek;

        Set<ZSetOperations.TypedTuple<String>> all = redisTemplate.opsForZSet()
                .rangeWithScores(key, 0, -1);

        if (all == null || all.isEmpty()) {
            return List.of();
        }

        List<Map<String, Object>> result = new ArrayList<>();

        for (ZSetOperations.TypedTuple<String> tuple : all) {
            Integer postId = Integer.parseInt(tuple.getValue());
            int likeCount = (int) tuple.getScore().doubleValue();

            Map<String, Object> entry = new HashMap<>();
            entry.put("postId", postId);
            entry.put("likeCount", likeCount);
            result.add(entry);
        }

        return result;
    }
    public List<Map<String, Object>> getAllCachedEventPosts() {
        int currentWeek = getCurrentWeek();
        String redisKey = "event:likes:week:" + currentWeek;

        Set<String> postIds = redisTemplate.opsForZSet().range(redisKey, 0, -1);
        if (postIds == null || postIds.isEmpty()) {
            return List.of();
        }

        List<String> keys = postIds.stream()
                .map(id -> "event:post:" + id)
                .toList();

        List<String> cachedJsonList = redisTemplate.opsForValue().multiGet(keys);

        List<Map<String, Object>> result = new ArrayList<>();

        for (int i = 0; i < postIds.size(); i++) {
            String json = cachedJsonList.get(i);
            if (json == null) continue;

            try {
                Map<String, Object> postData = objectMapper.readValue(json, Map.class);

                Double score = redisTemplate.opsForZSet().score(redisKey, postIds.toArray(new String[0])[i]);
                postData.put("likeCount", score == null ? 0 : score.intValue());

                result.add(postData);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    private int getCurrentWeek() {
        LocalDate start = LocalDate.of(2025, 6, 16);
        ZoneId koreaZone = ZoneId.of("Asia/Seoul");
        LocalDate now = LocalDate.now(koreaZone);
        return (int) ChronoUnit.WEEKS.between(start, now) + 1;
    }
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void cachePostWithUserInfo(EventPost post) {
        try {
            Map<String, Object> postData = new HashMap<>();
            postData.put("postId", post.getId());
            postData.put("imageUrl", post.getImageUrl());
            postData.put("nickname", post.getUser().getNickname());
            postData.put("profileImageUrl", post.getUser().getProfileImageUrl());
            postData.put("likeCount", 0);

            String redisKey = "event:post:" + post.getId();
            String json = objectMapper.writeValueAsString(postData);

            redisTemplate.opsForValue().set(redisKey, json);
        } catch (Exception e) {
            throw new RuntimeException("Redis 캐싱 실패", e);
        }
    }
}

