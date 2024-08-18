package kaboo.kaboochat.chat.repository;

import static org.junit.jupiter.api.Assertions.*;

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
		chatMessageRepository.save(ChatMessage.createMessage(request1));
		chatMessageRepository.save(ChatMessage.createMessage(request2));
		chatMessageRepository.save(ChatMessage.createMessage(request3));
		chatMessageRepository.save(ChatMessage.createMessage(request4));
		chatMessageRepository.save(ChatMessage.createMessage(request5));
		chatMessageRepository.save(ChatMessage.createMessage(request6));
		chatMessageRepository.save(ChatMessage.createMessage(request7));
		chatMessageRepository.save(ChatMessage.createMessage(request8));
		chatMessageRepository.save(ChatMessage.createMessage(request9));
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
