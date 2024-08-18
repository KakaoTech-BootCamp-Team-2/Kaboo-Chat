package kaboo.kaboochat.chat.domain.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 채팅방 Entity
 * <p>
 * 이 클래스는 채팅방을 RDB에 저장하기 위한 Entity입니다.
 * </p>
 *
 * @author : parkjihyeok
 * @since : 2024/08/18
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // createRoom 메서드로만 메시지 생성, Entity이기 때문에 PROTECTED로 설정
@AllArgsConstructor(access = AccessLevel.PRIVATE) // createRoom 메서드로만 메시지 생성
@Builder
public class ChatRoom {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "chat_room_id")
	private Long id; // 기본키
	@Column(name = "chat_room_name", nullable = false, updatable = false)
	private String chatRoomName; // 채팅방 이름
	@Column(name = "chat_room_uuid", nullable = false, updatable = false)
	private String chatRoomUUID; // 채팅방 UUID

	/**
	 * 채팅방을 생성합니다.
	 *
	 * @param chatRoomName 채팅방이름
	 * @return 생성한 채팅방 객체
	 */
	public static ChatRoom createRoom(String chatRoomName) {
		return ChatRoom.builder()
				.chatRoomName(chatRoomName)
				.chatRoomUUID(UUID.randomUUID().toString())
				.build();
	}
}
