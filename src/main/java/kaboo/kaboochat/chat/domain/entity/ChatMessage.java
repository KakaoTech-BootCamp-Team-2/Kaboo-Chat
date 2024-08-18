package kaboo.kaboochat.chat.domain.entity;

import java.time.LocalDateTime;

import org.springframework.data.mongodb.core.mapping.Document;

import kaboo.kaboochat.chat.domain.dto.request.ChatRequest;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 채팅 메시지 MongoDB Entity
 * <p>
 * 이 클래스는 MongoDB에 채팅 메시지를 기록하기 위한 Entity입니다.
 * </p>
 *
 * @author : parkjihyeok
 * @since : 2024/08/18
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE) // createMessage 메서드로만 메시지 생성
@AllArgsConstructor(access = AccessLevel.PRIVATE) // createMessage 메서드로만 메시지 생성
@Builder
@Document(collection = "chatMessage")
public class ChatMessage {

	private String chatRoomUUID; // 채팅방 UUID
	private String username; // 전송한 사용자의 ID
	private String nickname; // 전송한 사용자의 닉네임
	private String message; // 메시지 내용
	private LocalDateTime sendAt; // 메시지 전송시간

	public static ChatMessage createMessage(ChatRequest dto) {
		return ChatMessage.builder()
				.chatRoomUUID(dto.getChatRoomUUID())
				.username(dto.getUsername())
				.nickname(dto.getNickname())
				.message(dto.getMessage())
				.sendAt(LocalDateTime.now())
				.build();
	}
}
