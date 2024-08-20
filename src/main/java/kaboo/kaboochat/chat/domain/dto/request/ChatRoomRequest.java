package kaboo.kaboochat.chat.domain.dto.request;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 채팅방 생성 DTO
 * <p>
 * 채팅방을 만들기 위해선 채팅방의 참여자 정보와 채팅방의 이름이 필요합니다.
 * </p>
 *
 * @author : parkjihyeok
 * @since : 2024/08/18
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomRequest {

	private List<String> usernames;
	private String chatRoomName;
}
