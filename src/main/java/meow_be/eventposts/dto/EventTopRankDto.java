package meow_be.eventposts.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventTopRankDto {
    private Integer week;
    private Integer postId;
    private String imageUrl;
    private String nickname;
    private String profileImageUrl;
    private String animalType;
    private Integer likeCount;
    private Integer ranking;
}
