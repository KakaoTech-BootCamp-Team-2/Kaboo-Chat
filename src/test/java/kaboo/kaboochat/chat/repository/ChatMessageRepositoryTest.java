package kaboo.kaboochat.chat.repository;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.util.ReflectionTestUtils.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import kaboo.kaboochat.chat.domain.dto.request.ChatMessageRequest;
import kaboo.kaboochat.chat.domain.entity.ChatMessage;

/**
 * @author : parkjihyeok
 * @since : 2024/08/18
 */
@DataMongoTest
@DisplayName("채팅 메시지 Repository 테스트")
class ChatMessageRepositoryTest {

	@Autowired
	ChatMessageRepository chatMessageRepository;

	@BeforeEach
	void setUp() {
		// 테스트용 데이터 삽입
		chatMessageRepository.deleteAll(); // 초기화

		ChatMessageRequest request1 = new ChatMessageRequest("room1", "user1", "nick1", "안녕1!");
		ChatMessageRequest request2 = new ChatMessageRequest("room1", "user2", "nick2", "안녕2!");
		ChatMessageRequest request3 = new ChatMessageRequest("room1", "user3", "nick3", "안녕3!");
		ChatMessageRequest request4 = new ChatMessageRequest("room2", "user4", "nick4", "안녕4!");
		ChatMessageRequest request5 = new ChatMessageRequest("room2", "user5", "nick5", "안녕5!");
		ChatMessageRequest request6 = new ChatMessageRequest("room1", "user6", "nick6", "안녕6!");
		ChatMessageRequest request7 = new ChatMessageRequest("room1", "user7", "nick7", "안녕7!");
		ChatMessageRequest request8 = new ChatMessageRequest("room1", "user8", "nick8", "안녕8!");
		ChatMessageRequest request9 = new ChatMessageRequest("room1", "user9", "nick9", "안녕9!");
		ChatMessage m1 = ChatMessage.createMessage(request1);
		ChatMessage m2 = ChatMessage.createMessage(request2);
		ChatMessage m3 = ChatMessage.createMessage(request3);
		ChatMessage m4 = ChatMessage.createMessage(request4);
		ChatMessage m5 = ChatMessage.createMessage(request5);
		ChatMessage m6 = ChatMessage.createMessage(request6);
		ChatMessage m7 = ChatMessage.createMessage(request7);
		ChatMessage m8 = ChatMessage.createMessage(request8);
		ChatMessage m9 = ChatMessage.createMessage(request9);
		// 리플렉션을 사용해 시간 필드를 고정시켜 테스트의 일관성유지
		setField(m1, "sendAt", LocalDateTime.of(2024, 1, 1, 1, 1));
		setField(m2, "sendAt", LocalDateTime.of(2024, 1, 1, 1, 2));
		setField(m3, "sendAt", LocalDateTime.of(2024, 1, 1, 1, 3));
		setField(m4, "sendAt", LocalDateTime.of(2024, 1, 1, 1, 4));
		setField(m5, "sendAt", LocalDateTime.of(2024, 1, 1, 1, 5));
		setField(m6, "sendAt", LocalDateTime.of(2024, 1, 1, 1, 6));
		setField(m7, "sendAt", LocalDateTime.of(2024, 1, 1, 1, 7));
		setField(m8, "sendAt", LocalDateTime.of(2024, 1, 1, 1, 8));
		setField(m9, "sendAt", LocalDateTime.of(2024, 1, 1, 1, 9));
		chatMessageRepository.save(m1);
		chatMessageRepository.save(m2);
		chatMessageRepository.save(m3);
		chatMessageRepository.save(m4);
		chatMessageRepository.save(m5);
		chatMessageRepository.save(m6);
		chatMessageRepository.save(m7);
		chatMessageRepository.save(m8);
		chatMessageRepository.save(m9);
	}

	@AfterEach
	void tearDown() {
		chatMessageRepository.deleteAll(); // 초기화
	}

	@Test
	@DisplayName("채팅방 UUID와 페이지로 채팅기록 불러오기")
	void findByUUIDTest() {
		// Given
		String UUID = "room1";
		Pageable pageable = PageRequest.of(1, 3); // 두 번째 페이지 3개씩 자름 (6, 3, 2가 와야한다.)

		// When
		Page<ChatMessage> result = chatMessageRepository.findByChatRoomUUIDOrderBySendAtDesc(UUID, pageable);

		// Then
		assertEquals(7, result.getTotalElements());
		assertEquals(3, result.getContent().size());
		assertEquals(3, result.getTotalPages());
		assertEquals("안녕6!", result.getContent().get(0).getMessage());
		assertEquals("user3", result.getContent().get(1).getUsername());
		assertEquals("nick2", result.getContent().get(2).getNickname());
	}
}
