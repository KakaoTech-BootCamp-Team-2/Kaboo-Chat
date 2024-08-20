package kaboo.kaboochat.chat.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import kaboo.kaboochat.chat.domain.redis.RedisSubscriber;

/**
 * Redis 설정 클래스
 * <p>
 * 이 클래스는 Redis와의 연결을 설정하고, Redis의 Pub/Sub 기능을 사용하여 메시지를 처리하는 데 필요한 빈(Bean)을 정의합니다.
 * <br>
 * Lettuce 클라이언트를 사용하여 Redis와의 연결을 관리하고, RedisTemplate을 통해 Redis에 데이터를 직렬화/역직렬화하여 저장하거나 조회하며,
 * Pub/Sub 메시지를 처리하는 리스너와 채널을 설정합니다.
 * </p>
 * @author : parkjihyeok
 * @since : 2024/08/17
 */
@Configuration
public class RedisConfig {

	private final String redisHost;
	private final int redisPort;

	public RedisConfig(@Value("${REDIS_HOST}") String redisHost, @Value("${REDIS_PORT}") int redisPort) {
		this.redisHost = redisHost;
		this.redisPort = redisPort;
	}

	/**
	 * RedisConnectionFactory 빈 생성
	 * <p>
	 * Lettuce 클라이언트를 사용하여 Redis 서버와의 연결을 생성하고 관리합니다.
	 * <br>
	 * 이 빈은 Redis 서버와의 연결을 생성하고, 다른 Redis 관련 빈들이 이 연결을 사용하여 Redis와 통신하게 됩니다.
	 * </p>
	 * @return RedisConnectionFactory LettuceConnectionFactory 인스턴스
	 */
	@Bean
	public RedisConnectionFactory redisConnectionFactory() {
		return new LettuceConnectionFactory(redisHost, redisPort);
	}

	/**
	 * RedisTemplate 빈 생성
	 * <p>
	 * 이 템플릿은 Redis에서 데이터를 저장하고 조회하는 데 사용됩니다.
	 * 키는 문자열(String)로 직렬화하고, 값은 JSON 형식으로 직렬화하여 Redis에 저장합니다.
	 * <br>
	 * - 키 직렬화: StringRedisSerializer 사용 <br>
	 * - 값 직렬화: GenericJackson2JsonRedisSerializer 사용 <br>
	 * - 해시 키 직렬화: StringRedisSerializer 사용 <br>
	 * - 해시 값 직렬화: GenericJackson2JsonRedisSerializer 사용
	 * </p>
	 * @param connectionFactory RedisConnectionFactory 주입된 RedisConnectionFactory 인스턴스
	 * @return RedisTemplate<String, Object> Redis에서 데이터를 처리하는 데 사용되는 템플릿
	 */
	@Bean
	public RedisTemplate<String, Object> redisChatTemplate(RedisConnectionFactory connectionFactory) {
		RedisTemplate<String, Object> template = new RedisTemplate<>();
		template.setConnectionFactory(connectionFactory); // Redis 연결 팩토리 설정
		template.setKeySerializer(new StringRedisSerializer()); // 키를 문자열로 직렬화
		template.setValueSerializer(new GenericJackson2JsonRedisSerializer()); // 값을 JSON 형식으로 직렬화
		template.setHashKeySerializer(new StringRedisSerializer()); // 해시 구조의 키를 문자열로 직렬화
		template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer()); // 해시 구조의 값을 JSON 형식으로 직렬화
		template.afterPropertiesSet(); // 설정된 직렬화기들을 적용하여 템플릿 초기화

		return template;
	}

	/**
	 * Pub/Sub 채널 정의
	 * <p>
	 * Redis의 Pub/Sub 모델에서 사용될 채널을 정의합니다. 이 채널은 "chatroom"이라는 이름을 가지며,
	 * 메시지가 이 채널로 발행되면 이를 구독하고 있는 모든 구독자에게 메시지가 전달됩니다.
	 * </p>
	 * @return ChannelTopic "chatroom"이라는 이름을 가진 채널
	 */
	@Bean
	public ChannelTopic channelTopic() {
		return new ChannelTopic("chatroom");
	}

	/**
	 * RedisMessageListenerContainer 빈 생성
	 * <p>
	 * Redis의 Pub/Sub 메시지를 수신하고 처리하기 위한 리스너 컨테이너를 설정합니다. <br>
	 * 이 컨테이너는 Redis 서버에서 발행된 메시지를 실시간으로 수신하고, 지정된 리스너(MessageListenerAdapter)를 통해 메시지를 처리합니다.
	 * </p>
	 * @param listenerAdapterChatMessage Redis에서 수신된 메시지를 처리하는 리스너 어댑터
	 * @param channelTopic 메시지를 구독할 채널
	 * @return RedisMessageListenerContainer 메시지 리스너 컨테이너
	 */
	@Bean
	public RedisMessageListenerContainer redisMessageListener(
			MessageListenerAdapter listenerAdapterChatMessage,
			ChannelTopic channelTopic
	) {
		RedisMessageListenerContainer container = new RedisMessageListenerContainer();
		container.setConnectionFactory(redisConnectionFactory()); // Redis 연결 팩토리 설정
		container.addMessageListener(listenerAdapterChatMessage, channelTopic); // 리스너와 채널을 컨테이너에 등록
		return container;
	}

	/**
	 * MessageListenerAdapter 빈 생성
	 * <p>
	 * 이 어댑터는 Redis에서 수신된 메시지를 처리하는 역할을 합니다. <br>
	 * 여기서는 RedisSubscriber 클래스의 "sendMessage" 메서드를 메시지 처리 메서드로 설정합니다. <br>
	 * 즉, Redis에서 메시지가 수신되면, 이 메서드가 호출되어 메시지를 처리합니다.
	 * </p>
	 * @param subscriber 실제로 메시지를 처리하는 클래스 (RedisSubscriber)
	 * @return MessageListenerAdapter 메시지 처리 어댑터
	 */
	@Bean
	public MessageListenerAdapter listenerAdapterChatMessage(RedisSubscriber subscriber) {
		// RedisSubscriber 클래스에 정의된 메서드 이름을 2번째 인자로 넘겨야 한다.
		return new MessageListenerAdapter(subscriber, "sendMessage");
	}
}
