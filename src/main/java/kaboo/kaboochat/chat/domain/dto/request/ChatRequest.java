package kaboo.kaboochat.chat.domain.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 채팅 메시지 DTO
 *
 * @author : parkjihyeok
 * @since : 2024/08/17
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatRequest {

	private String chatRoomUUID; // 채팅방 UUID
	private String username; // 전송한 사용자의 ID
	private String nickname; // 전송한 사용자의 닉네임
	private String message; // 메시지 내용
}
