package kaboo.kaboochat.chat.domain.redis;

import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessageSendingOperations;

import com.fasterxml.jackson.databind.ObjectMapper;

import kaboo.kaboochat.chat.domain.dto.request.ChatRequest;

/**
 * @author : parkjihyeok
 * @since : 2024/08/17
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("RedisSub 테스트")
class RedisSubscriberTest {

	@InjectMocks
	private RedisSubscriber redisSubscriber;
	@Mock
	private ObjectMapper objectMapper;
	@Mock
	private SimpMessageSendingOperations messagingTemplate;

	@Test
	@DisplayName("웹 소켓 클라이언트 전송 테스트 (성공)")
	void subscriberTest() throws Exception {
		// Given
		ChatRequest req = new ChatRequest("AAA-BBB", "pjh5365", "안녕하세요!", LocalDateTime.of(2024, 1, 1, 12, 40));
		given(objectMapper.readValue(anyString(), eq(ChatRequest.class))).willReturn(req);

		// When
		redisSubscriber.sendMessage("any string");

		// Then
		verify(messagingTemplate).convertAndSend("/sub/chat/room/AAA-BBB", req);
	}

	@Test
	@DisplayName("웹 소켓 클라이언트 전송 테스트 (실패)")
	void subscriberFailTest() throws Exception {
		// Given
		ChatRequest req = new ChatRequest("AAA-BBB", "pjh5365", "안녕하세요!", LocalDateTime.of(2024, 1, 1, 12, 40));
		given(objectMapper.readValue(anyString(), eq(ChatRequest.class))).willThrow(
				new RuntimeException("Something Wrong"));

		// When
		redisSubscriber.sendMessage("any string");

		// Then
		verify(messagingTemplate, never()).convertAndSend("/sub/chat/room/AAA-BBB", req);
	}
}
