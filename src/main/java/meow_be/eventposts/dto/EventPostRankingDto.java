package meow_be.eventposts.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventPostRankingDto {
    private Integer postId;
    private String imageUrl;
    private String nickname;
    private String animalType;
    private String profileImageUrl;
    private Integer userId;
    private Integer likeCount;
}
