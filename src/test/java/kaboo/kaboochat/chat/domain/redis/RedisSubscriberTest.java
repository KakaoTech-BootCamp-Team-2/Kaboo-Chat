package kaboo.kaboochat.chat.domain.redis;

import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessageSendingOperations;

import com.fasterxml.jackson.databind.ObjectMapper;

import kaboo.kaboochat.chat.domain.dto.request.ChatMessageRequest;

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
		ChatMessageRequest req = new ChatMessageRequest("AAA-BBB", "pjh5365", "justin", "안녕하세요!");
		given(objectMapper.readValue(anyString(), eq(ChatMessageRequest.class))).willReturn(req);

		// When
		redisSubscriber.sendMessage("any string");

		// Then
		verify(messagingTemplate).convertAndSend("/sub/chat/room/AAA-BBB", req);
	}

	@Test
	@DisplayName("웹 소켓 클라이언트 전송 테스트 (실패)")
	void subscriberFailTest() throws Exception {
		// Given
		ChatMessageRequest req = new ChatMessageRequest("AAA-BBB", "pjh5365", "justin", "안녕하세요!");
		given(objectMapper.readValue(anyString(), eq(ChatMessageRequest.class)))
				.willThrow(new RuntimeException("Something Wrong"));

		// When
		redisSubscriber.sendMessage("any string");

		// Then
		verify(messagingTemplate, never()).convertAndSend("/sub/chat/room/AAA-BBB", req);
	}
}
