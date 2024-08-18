package kaboo.kaboochat.chat.domain.redis;

import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import kaboo.kaboochat.chat.domain.dto.request.ChatRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Redis에서 구독된 메시지를 처리하고 웹 소켓 클라이언트에게 전송하는 클래스
 * <p>
 * 이 클래스는 Redis에서 발행된 메시지를 대기하고 DTO로 변환하여, 해당 채팅방을 구독하고 있는 웹 소켓 클라이언트에게 전송합니다.
 * </p>
 *
 * @author : parkjihyeok
 * @since : 2024/08/17
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RedisSubscriber {

	private final ObjectMapper objectMapper;
	private final SimpMessageSendingOperations messagingTemplate;

	/**
	 * Redis 채널에서 수신된 메시지를 처리합니다.
	 * <p>
	 * 이 메서드는 Redis에서 발행된 메시지를 수신했을 때 호출됩니다. <br>
	 * 메시지를 JSON 문자열에서 DTO 객체로 변환한 하여 정보를 추출하고, 해당 채팅방을 구독 중인 WebSocket 클라이언트에게 전송합니다.
	 * </p>
	 *
	 * @param chatRequest Redis에서 수신된 JSON 문자열 형식의 메시지.
	 */
	public void sendMessage(String chatRequest) {
		try {
			ChatRequest chat = objectMapper.readValue(chatRequest, ChatRequest.class);
			// 채팅방을 구독 중인 WebSocket 클라이언트에게 메시지 전송
			messagingTemplate.convertAndSend("/sub/chat/room/" + chat.getChatRoomUUID(), chat);
		} catch (Exception e) {
			log.error("[Kaboo-Chat]: 예상치 못한 예외가 발생하였습니다. 내용 = {}", e.getMessage());
		}
	}
}
