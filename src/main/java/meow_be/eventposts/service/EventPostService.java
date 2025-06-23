package meow_be.eventposts.service;

import com.amazonaws.services.kms.model.NotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import meow_be.eventposts.domain.EventPost;
import meow_be.eventposts.domain.EventWeek;
import meow_be.eventposts.dto.EventPostRankingDto;
import meow_be.eventposts.dto.EventTopRankDto;
import meow_be.eventposts.repository.EventPostQueryRepository;
import meow_be.eventposts.repository.EventPostRepository;
import meow_be.eventposts.repository.EventWeekRepository;
import meow_be.users.domain.User;
import meow_be.users.repository.UserRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;
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
    private final EventPostQueryRepository eventPostQueryRepository;
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

        Double newScore = redisTemplate.opsForZSet().incrementScore(key, postId.toString(), 1);

        String redisPostKey = "event:post:" + postId;
        String json = redisTemplate.opsForValue().get(redisPostKey);

        if (json != null && newScore != null) {
            try {
                Map<String, Object> postData = objectMapper.readValue(json, Map.class);
                postData.put("likeCount", newScore.intValue());
                String updatedJson = objectMapper.writeValueAsString(postData);
                redisTemplate.opsForValue().set(redisPostKey, updatedJson);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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

        String triggerKey = "saveWeeklyRankingTrigger:" + currentWeek;
        Boolean hasKey = redisTemplate.hasKey(triggerKey);
        if (Boolean.FALSE.equals(hasKey)) {
            LocalDateTime voteEnd = eventWeek.getEndVoteAt().plusMinutes(1);
            long secondsUntilExpire = java.time.Duration.between(LocalDateTime.now(ZoneId.of("Asia/Seoul")), voteEnd).getSeconds();

            if (secondsUntilExpire > 0) {
                redisTemplate.opsForValue().set(triggerKey, "1" , java.time.Duration.ofSeconds(secondsUntilExpire));
            } else {
                saveWeeklyRanking(currentWeek);
            }
        }

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

        List<String> redisPostKeys = new ArrayList<>();
        for (ZSetOperations.TypedTuple<String> tuple : top3) {
            redisPostKeys.add("event:post:" + tuple.getValue());
        }
        List<String> jsonList = redisTemplate.opsForValue().multiGet(redisPostKeys);

        for (int i = 0; i < jsonList.size(); i++) {
            String json = jsonList.get(i);
            if (json == null) continue;

            try {
                Map<String, Object> postData = objectMapper.readValue(json, Map.class);
                Integer postId = Integer.parseInt(top3.stream().skip(i).findFirst().get().getValue());
                int likeCount = (int) top3.stream().skip(i).findFirst().get().getScore().doubleValue();

                Map<String, Object> entry = new HashMap<>();
                entry.put("postId", postId);
                entry.put("ranking", ranking++);
                entry.put("likeCount", likeCount);
                entry.put("profileImageUrl", postData.get("profileImageUrl"));
                entry.put("animalType", postData.get("animalType"));
                entry.put("imageUrl", postData.get("imageUrl"));
                entry.put("userId", postData.get("userId"));
                entry.put("nickname", postData.get("nickname"));

                result.add(entry);
            } catch (Exception e) {
                e.printStackTrace();
            }
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

        Set<ZSetOperations.TypedTuple<String>> tuples =
                redisTemplate.opsForZSet().reverseRangeWithScores(redisKey, 0, -1);

        if (tuples == null || tuples.isEmpty()) {
            return List.of();
        }
        
        List<String> postIds = new ArrayList<>();
        Map<String, Integer> likeCountMap = new HashMap<>();
        for (ZSetOperations.TypedTuple<String> tuple : tuples) {
            String postId = tuple.getValue();
            if (postId != null) {
                postIds.add("event:post:" + postId);
                likeCountMap.put(postId, tuple.getScore() == null ? 0 : tuple.getScore().intValue());
            }
        }

        List<String> jsonList = redisTemplate.opsForValue().multiGet(postIds);

        List<Map<String, Object>> result = new ArrayList<>();
        if (jsonList != null) {
            for (int i = 0; i < jsonList.size(); i++) {
                String json = jsonList.get(i);
                if (json == null) continue;

                try {
                    Map<String, Object> postData = objectMapper.readValue(json, Map.class);
                    String fullKey = postIds.get(i);
                    String postId = fullKey.substring("event:post:".length());
                    postData.put("likeCount", likeCountMap.getOrDefault(postId, 0));
                    result.add(postData);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return result;
    }
    public List<EventPostRankingDto> getRankedPostsByWeek(int week) {
        return eventPostQueryRepository.findRankedPostsByWeek(week);
    }

    public List<EventTopRankDto> getAllTop3Rankings() {
        return eventPostQueryRepository.findTop3RankedPostsByWeek();
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
            postData.put("userId", post.getUser().getId());
            postData.put("animalType", post.getUser().getAnimalType());

            String redisKey = "event:post:" + post.getId();
            String json = objectMapper.writeValueAsString(postData);

            redisTemplate.opsForValue().set(redisKey, json, Duration.ofDays(7));
        } catch (Exception e) {
            throw new RuntimeException("Redis 캐싱 실패", e);
        }
    }
}

