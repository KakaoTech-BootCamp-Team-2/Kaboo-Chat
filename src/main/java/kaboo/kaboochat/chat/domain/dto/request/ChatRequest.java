package kaboo.kaboochat.chat.domain.dto.request;

import java.time.LocalDateTime;

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

	private String roomId;        // 채팅방 번호
	private String userId; // 채팅을 전송한 유저
	private String message; // 채팅 내용
	private LocalDateTime date; // 전송 시간
}
