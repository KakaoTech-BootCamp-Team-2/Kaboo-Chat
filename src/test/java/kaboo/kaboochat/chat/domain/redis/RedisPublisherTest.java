package kaboo.kaboochat.chat.domain.redis;

import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

import kaboo.kaboochat.chat.domain.dto.request.ChatMessageRequest;

/**
 * @author : parkjihyeok
 * @since : 2024/08/17
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("RedisPub 테스트")
class RedisPublisherTest {

	@InjectMocks
	private RedisPublisher redisPublisher;
	@Mock
	private RedisTemplate<String, Object> redisTemplate;
	@Mock
	private ChannelTopic channelTopic;

	@Test
	@DisplayName("Redis발행 테스트")
	void publisherTest() {
		// Given
		given(channelTopic.getTopic()).willReturn("chatroom");
		ChatMessageRequest req = new ChatMessageRequest("AAA-BBB", "pjh5365", "justin", "안녕하세요!");

		// When
		redisPublisher.publish(req);

		// Then
		verify(redisTemplate).convertAndSend("chatroom", req);
	}
}
