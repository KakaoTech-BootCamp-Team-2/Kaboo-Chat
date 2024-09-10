package kaboo.kaboochat.chat.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import kaboo.kaboochat.chat.domain.dto.request.ChatMessageRequest;
import kaboo.kaboochat.chat.domain.dto.request.ChatRoomRequest;
import kaboo.kaboochat.chat.domain.dto.response.ChatMessageResponse;
import kaboo.kaboochat.chat.domain.dto.response.ChatRoomResponse;
import kaboo.kaboochat.chat.domain.entity.ChatMember;
import kaboo.kaboochat.chat.domain.entity.ChatMessage;
import kaboo.kaboochat.chat.domain.entity.ChatRoom;
import kaboo.kaboochat.chat.domain.entity.Member;
import kaboo.kaboochat.chat.domain.redis.RedisPublisher;
import kaboo.kaboochat.chat.repository.ChatMemberRepository;
import kaboo.kaboochat.chat.repository.ChatMessageRepository;
import kaboo.kaboochat.chat.repository.ChatRoomRepository;
import kaboo.kaboochat.chat.repository.MemberRepository;

/**
 * @author : parkjihyeok
 * @since : 2024/08/18
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("채팅 서비스 테스트")
class ChatServiceTest {

	@InjectMocks
	ChatService chatService;
	@Mock
	RedisPublisher redisPublisher;
	@Mock
	MemberRepository memberRepository;
	@Mock
	ChatRoomRepository chatRoomRepository;
	@Mock
	ChatMemberRepository chatMemberRepository;
	@Mock
	ChatMessageRepository chatMessageRepository;

	// 회원에 대한 생성자를 전부 막았으므로 모킹한다.
	Member member1 = Mockito.mock(Member.class);
	Member member2 = Mockito.mock(Member.class);
	Member member3 = Mockito.mock(Member.class);

	@Test
	@DisplayName("채팅방 UUID값으로 채팅방 DTO 생성")
	void findByChatUUIDTest() {
		// Given
		String roomUUID = "room-uuid";
		ChatRoom chatRoom = ChatRoom.createRoom("채팅방1");

		given(member1.getUsername()).willReturn("user1");
		given(member2.getUsername()).willReturn("user2");
		given(member3.getUsername()).willReturn("user3");

		given(chatRoomRepository.findByRoomUUID(roomUUID)).willReturn(Optional.of(chatRoom));
		given(chatMemberRepository.findByChatRoomUUID(roomUUID))
				.willReturn(List.of(
						ChatMember.createChatMember(member1, chatRoom),
						ChatMember.createChatMember(member2, chatRoom),
						ChatMember.createChatMember(member3, chatRoom)
				));

		// When
		ChatRoomResponse result = chatService.findByChatUUID(roomUUID);

		// Then
		assertEquals(3, result.getUsernames().size());
		assertEquals("채팅방1", result.getChatRoomName());
		assertEquals("user1", result.getUsernames().get(0));
		assertEquals("user2", result.getUsernames().get(1));
		assertEquals("user3", result.getUsernames().get(2));
		verify(chatRoomRepository).findByRoomUUID(any());
		verify(chatMemberRepository).findByChatRoomUUID(any());
	}

	@Test
	@DisplayName("채팅방 UUID값으로 채팅방 DTO 생성 실패")
	void findByChatUUIDFailTest() {
		// Given

		// When

		// Then
		assertThrows(IllegalArgumentException.class, () -> chatService.findByChatUUID("room-uuid"));
		verify(chatRoomRepository).findByRoomUUID(any());
		verify(chatMemberRepository, never()).findByChatRoomUUID(any());
	}

	@Test
	@DisplayName("사용자가 참여중인 채팅방 리스트 반환")
	void findByUsernameTest1() {
		// Given
		given(chatRoomRepository.findByUsername("user1"))
				.willReturn(List.of(
						ChatRoom.createRoom("채팅방1"),
						ChatRoom.createRoom("채팅방2"),
						ChatRoom.createRoom("채팅방3"),
						ChatRoom.createRoom("채팅방4")
				));

		// When
		List<ChatRoomResponse> result = chatService.findByUsername("user1");

		// Then
		assertEquals(4, result.size());
		assertEquals("채팅방2", result.get(1).getChatRoomName());
		verify(chatRoomRepository).findByUsername("user1");
	}

	@Test
	@DisplayName("사용자가 참여중인 채팅방 리스트 반환 0")
	void findByUsernameTest2() {
		// Given
		given(chatRoomRepository.findByUsername("user1")).willReturn(List.of());

		// When
		List<ChatRoomResponse> result = chatService.findByUsername("user1");

		// Then
		assertEquals(0, result.size());
		verify(chatRoomRepository).findByUsername("user1");
	}

	@Test
	@DisplayName("채팅방 생성 테스트")
	void createRoomTest() {
		// Given
		ChatRoomRequest request = new ChatRoomRequest(List.of("user1", "user2", "user3"), "채팅방1");
		given(memberRepository.findByUsername("user1")).willReturn(Optional.of(member1));
		given(memberRepository.findByUsername("user2")).willReturn(Optional.of(member2));
		given(memberRepository.findByUsername("user3")).willReturn(Optional.of(member3));

		// When

		// Then
		assertDoesNotThrow(() -> chatService.createRoom(request));
		verify(memberRepository, times(3)).findByUsername(any());
		verify(chatMemberRepository, times(3)).save(any());
	}

	@Test
	@DisplayName("채팅방 생성 실패 테스트")
	void createRoomFailTest() {
		// Given
		ChatRoomRequest request = new ChatRoomRequest(List.of("user1", "user2", "user3"), "채팅방1");
		given(memberRepository.findByUsername("user1")).willReturn(Optional.of(member1));
		given(memberRepository.findByUsername("user2")).willReturn(Optional.empty());

		// When

		// Then
		assertThrows(IllegalArgumentException.class, () -> chatService.createRoom(request));
		verify(memberRepository, times(2)).findByUsername(any()); // 2번째 실행때 롤백됨
		verify(chatMemberRepository).save(any()); // 최초 1번만 실행되고 이후 롤백된다.
	}

	@Test
	@DisplayName("채팅내역 조회 테스트")
	void findMessageTest1() {
		// Given
		String uuid = "room-uuid";
		Pageable pageable = PageRequest.of(0, 3);
		ChatMessageRequest request1 = new ChatMessageRequest(uuid, "user1", "nick1", "메시지1");
		ChatMessageRequest request2 = new ChatMessageRequest(uuid, "user1", "nick1", "메시지2");
		ChatMessageRequest request3 = new ChatMessageRequest(uuid, "user2", "nick2", "메시지3");
		ChatMessageRequest request4 = new ChatMessageRequest(uuid, "user1", "nick1", "메시지4");
		given(chatMessageRepository.findByChatRoomUUIDOrderBySendAtDesc(uuid, pageable))
				.willReturn(new PageImpl<>(List.of(
						ChatMessage.createMessage(request1),
						ChatMessage.createMessage(request2),
						ChatMessage.createMessage(request3),
						ChatMessage.createMessage(request4)
				)));

		// When
		List<ChatMessageResponse> result = chatService.findMessage(uuid, pageable);

		// Then
		assertEquals(4, result.size());
		assertEquals("메시지1", result.get(0).getMessage());
		assertEquals("user2", result.get(2).getUsername());
		assertEquals("nick1", result.get(3).getKoreaName());
		verify(chatMessageRepository).findByChatRoomUUIDOrderBySendAtDesc(any(), any());
	}

	@Test
	@DisplayName("채팅내역 조회 테스트 0")
	void findMessageTest2() {
		// Given
		String uuid = "room-uuid";
		Pageable pageable = PageRequest.of(0, 3);
		given(chatMessageRepository.findByChatRoomUUIDOrderBySendAtDesc(uuid, pageable))
				.willReturn(new PageImpl<>(List.of()));

		// When
		List<ChatMessageResponse> result = chatService.findMessage(uuid, pageable);

		// Then
		assertEquals(0, result.size());
		verify(chatMessageRepository).findByChatRoomUUIDOrderBySendAtDesc(any(), any());
	}

	@Test
	@DisplayName("메시지 발송 테스트")
	void sendChatMessageTest() {
	    // Given
		String uuid = "room-uuid";
		ChatMessageRequest request = new ChatMessageRequest(uuid, "user1", "nick1", "메시지1");

	    // When
		chatService.sendChatMessage(request);

	    // Then
		verify(chatMessageRepository).save(any());
		verify(redisPublisher).publish(any());
	}

	@Test
	@DisplayName("메시지 발송 실패 테스트")
	void sendChatMessageFailTest() {
		// Given
		given(chatMessageRepository.save(any())).willThrow(new RuntimeException());
		ChatMessageRequest request = new ChatMessageRequest("uuid", "user1", "nick1", "메시지1");

		// When
		assertThrows(RuntimeException.class, () -> chatService.sendChatMessage(request));

		// Then
		verify(redisPublisher, never()).publish(any());
	}
}
