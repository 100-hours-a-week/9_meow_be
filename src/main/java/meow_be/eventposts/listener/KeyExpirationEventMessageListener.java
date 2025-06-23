/*
package meow_be.eventposts.listener;

import lombok.RequiredArgsConstructor;
import meow_be.eventposts.service.EventPostService;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KeyExpirationEventMessageListener implements MessageListener {

    private final EventPostService eventPostService;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String expiredKey = message.toString();

        if (expiredKey.startsWith("saveWeeklyRankingTrigger:")) {
            String weekStr = expiredKey.substring("saveWeeklyRankingTrigger:".length());
            try {
                int week = Integer.parseInt(weekStr);
                eventPostService.saveWeeklyRanking(week);
            } catch (NumberFormatException e) {
                System.err.println("잘못된 주차 키 형식: " + expiredKey);
            }
        }
    }
}
*/
