package meow_be.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChatRoomInfoDto {
    private Integer id;
    private String title;
}
