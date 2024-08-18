package kaboo.kaboochat.chat.domain.redis;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

import kaboo.kaboochat.chat.domain.dto.request.ChatMessageRequest;
import lombok.RequiredArgsConstructor;

/**
 * 레디스의 구독을 담당하는 클래스
 * <p>
 * 이 클래스는 특정 Redis 채널로 메시지를 발행하는 역할을 합니다.
 * </p>
 *
 * @author : parkjihyeok
 * @since : 2024/08/17
 */
@Service
@RequiredArgsConstructor
public class RedisPublisher {

	private final ChannelTopic channelTopic;
	private final RedisTemplate<String, Object> redisTemplate;

	/**
	 * 지정된 Redis 채널로 메시지를 발행합니다.
	 *
	 * @param request 발행할 메시지 데이터를 담은 DTO
	 */
	public void publish(ChatMessageRequest request) {
		redisTemplate.convertAndSend(channelTopic.getTopic(), request);
	}
}
