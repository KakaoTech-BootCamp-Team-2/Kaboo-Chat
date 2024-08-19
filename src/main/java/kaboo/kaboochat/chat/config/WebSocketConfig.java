package kaboo.kaboochat.chat.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * 웹 소켓 설정 클래스
 * <p>
 * 이 클래스는 웹 소켓과 관련된 설정을 정의합니다.
 * </p>
 * @author : parkjihyeok
 * @since : 2024/08/17
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

	/**
	 * 메시지 브로커 구성
	 * <p>
	 * 이 메서드는 클라이언트에게 메시지를 전달하기 위한 메시지 브로커를 설정합니다. <br>
	 * - enableSimpleBroker("/sub"): 간단한 메시지 브로커를 활성화합니다. &rarr;
	 *   클라이언트는 "/sub"로 시작하는 경로를 구독(subscribe)하여 메시지를 받을 수 있습니다. <br>
	 * - setApplicationDestinationPrefixes("/pub"): 클라이언트가 서버로 메시지를 보낼 때 사용할 경로의 접두사를 정의합니다. &rarr;
	 *   클라이언트는 "/pub"로 시작하는 경로를 통해 메시지를 전송하며, 서버는 이 경로를 통해 메시지를 수신하고 처리합니다. <br>
	 * <br>
	 * 예를 들어, 클라이언트가 "/pub/message"로 메시지를 보내면, 서버는 해당 메시지를 처리하고,
	 * "/sub/response" 경로로 메시지를 브로드캐스트할 수 있습니다.
	 * </p>
	 * @param registry MessageBrokerRegistry 메시지 브로커를 설정하는 데 사용되는 객체
	 */
	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {
		registry.enableSimpleBroker("/sub");
		registry.setApplicationDestinationPrefixes("/pub");
	}

	/**
	 * STOMP 엔드포인트 등록
	 * <p>
	 * 이 메서드는 클라이언트가 WebSocket 서버에 연결하기 위한 엔드포인트를 정의합니다. <br>
	 * - addEndpoint("/chat/ws"): "/chat/ws" 경로로 클라이언트가 WebSocket 연결을 할 수 있는 엔드포인트를 생성합니다. <br>
	 * - setAllowedOriginPatterns("*"): 모든 도메인에서의 요청을 허용합니다. (추후 특정 도메인만 허용하도록 수정) <br>
	 * - withSockJS(): SockJS를 사용하여 WebSocket을 지원하지 않는 브라우저에서도 폴백 메커니즘을 통해 WebSocket 기능을 사용할 수 있게 합니다. <br>
	 * <br>
	 * 클라이언트는 이 엔드포인트를 통해 WebSocket 연결을 설정하고, 서버와 실시간으로 메시지를 주고받을 수 있습니다.
	 * </p>
	 * @param registry StompEndpointRegistry 클라이언트의 WebSocket 연결을 관리하는 객체
	 */
	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/chat/ws")  // WebSocket 엔드포인트를 "/chat/ws"로 설정
				.setAllowedOriginPatterns("*")  // 모든 도메인에서의 WebSocket 연결 허용
				.withSockJS();  // SockJS를 활성화하여 WebSocket을 지원하지 않는 브라우저에서도 사용 가능
	}
}
