package kaboo.kaboochat.chat.domain.dto.response;

import java.util.List;

import kaboo.kaboochat.chat.domain.entity.ChatRoom;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * 채팅방의 정보를 담은 DTO
 *
 * @author : parkjihyeok
 * @since : 2024/08/18
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class ChatRoomResponse {

	private List<String> usernames;
	private String chatRoomUUID;
	private String chatRoomName;

	/**
	 * 하나의 채팅방에 대한 정보를 담은 객체를 생성해 반환합니다.
	 *
	 * @param usernames 채팅방 참여자 정보
	 * @param chatRoom 채팅방 Entity
	 * @return 채팅방 정보를 담은 DTO
	 */
	public static ChatRoomResponse fromEntity(List<String> usernames, ChatRoom chatRoom) {
		return ChatRoomResponse.builder()
				.usernames(usernames)
				.chatRoomUUID(chatRoom.getChatRoomUUID())
				.chatRoomName(chatRoom.getChatRoomName())
				.build();
	}

	/**
	 * 채팅방 Entity List를 전달받아 채팅방의 간단한 정보만 담고있는 리스트를 반환합니다.
	 *
	 * @param chatRooms 채팅방 Entity List
	 * @return 생성된 List
	 */
	public static List<ChatRoomResponse> fromEntityList(List<ChatRoom> chatRooms) {
		return chatRooms.stream()
				.map(cr -> ChatRoomResponse.builder()
						.chatRoomUUID(cr.getChatRoomUUID())
						.chatRoomName(cr.getChatRoomName())
						.build())
				.toList();
	}
}
