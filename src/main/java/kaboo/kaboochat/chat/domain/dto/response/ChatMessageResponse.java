package kaboo.kaboochat.chat.domain.dto.response;

import java.time.LocalDateTime;

import kaboo.kaboochat.chat.domain.entity.ChatMessage;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * 채팅 메시지를 담은 DTO
 *
 * @author : parkjihyeok
 * @since : 2024/08/18
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class ChatMessageResponse {

	private String username; // 전송한 사용자의 ID
	private String nickname; // 전송한 사용자의 닉네임
	private String message; // 메시지 내용
	private LocalDateTime sendAt; // 메시지 전송시간

	public static ChatMessageResponse fromEntity(ChatMessage chatMessage) {
		return ChatMessageResponse.builder()
				.username(chatMessage.getUsername())
				.nickname(chatMessage.getNickname())
				.message(chatMessage.getMessage())
				.sendAt(chatMessage.getSendAt())
				.build();
	}
}
