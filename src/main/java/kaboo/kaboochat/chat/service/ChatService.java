package kaboo.kaboochat.chat.service;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kaboo.kaboochat.chat.domain.dto.request.ChatMessageRequest;
import kaboo.kaboochat.chat.domain.dto.request.ChatRoomRequest;
import kaboo.kaboochat.chat.domain.dto.response.ChatMessageResponse;
import kaboo.kaboochat.chat.domain.dto.response.ChatRoomResponse;
import kaboo.kaboochat.chat.domain.entity.ChatMember;
import kaboo.kaboochat.chat.domain.entity.ChatMessage;
import kaboo.kaboochat.chat.domain.entity.ChatRoom;
import kaboo.kaboochat.chat.domain.redis.RedisPublisher;
import kaboo.kaboochat.chat.repository.ChatMemberRepository;
import kaboo.kaboochat.chat.repository.ChatMessageRepository;
import kaboo.kaboochat.chat.repository.ChatRoomRepository;
import kaboo.kaboochat.chat.repository.MemberRepository;
import lombok.RequiredArgsConstructor;

/**
 * 채팅을 담당하는 Service
 * <p>
 * 이 클래스는 채팅방을 생성, 조회하며 채팅방에 채팅을 발송합니다.
 * </p>
 *
 * @author : parkjihyeok
 * @since : 2024/08/18
 */
@Service
@RequiredArgsConstructor
public class ChatService {

	private final RedisPublisher redisPublisher;
	private final MemberRepository memberRepository;
	private final ChatRoomRepository chatRoomRepository;
	private final ChatMemberRepository chatMemberRepository;
	private final ChatMessageRepository chatMessageRepository;

	/**
	 * 채팅방 UUID값으로 채팅방의 정보를 찾아 반환합니다.
	 *
	 * @param chatRoomUUID 채팅방 UUID
	 * @return 채팅방의 정보를 담은 DTO
	 */
	public ChatRoomResponse findByChatUUID(String chatRoomUUID) {
		ChatRoom chatRoom = chatRoomRepository.findByRoomUUID(chatRoomUUID)
				.orElseThrow(() -> new IllegalArgumentException("해당 UUID에 해당하는 채팅방을 찾을 수 없습니다."));

		List<String> usernames = chatMemberRepository.findByChatRoomUUID(chatRoomUUID)
				.stream()
				.map(cm -> cm.getMember().getUsername())
				.toList();

		return ChatRoomResponse.fromEntity(usernames, chatRoom);
	}

	/**
	 * 사용자가 참여중인 채팅방의 정보를 찾아 List로 반환합니다.
	 *
	 * @param username 사용자 ID
	 * @return 채팅방 List
	 */
	public List<ChatRoomResponse> findByUsername(String username) {
		return ChatRoomResponse.fromEntityList(chatRoomRepository.findByUsername(username));
	}

	/**
	 * 새로운 채팅방을 생성합니다.
	 * <p>
	 * 새로운 채팅방을 생성하고 입력받은 사용자들을 DB에 검색하여 모두 채팅방 참여자로 추가합니다. <br>
	 * 단, 입력받은 사용자중 1명이라도 검색에 실패하면 채팅방 생성에 실패합니다.
	 * </p>
	 *
	 * @param request 채팅방 생성 DTO
	 */
	@Transactional
	public void createRoom(ChatRoomRequest request) {
		ChatRoom chatRoom = ChatRoom.createRoom(request.getChatRoomName());
		chatRoomRepository.save(chatRoom);
		request.getUsernames()
				.stream()
				.map(u -> memberRepository.findByUsername(u)
						.orElseThrow(() -> new IllegalArgumentException("해당하는 회원정보를 찾을 수 없습니다.")))
				.forEach(m -> chatMemberRepository.save(ChatMember.createChatMember(m, chatRoom)));
	}

	/**
	 * 채팅방의 채팅 내역을 불러옵니다.
	 *
	 * @param roomUUID 채팅방 UUID
	 * @param pageable 페이지 정보
	 * @return 해당하는 채팅 내역
	 */
	public List<ChatMessageResponse> findMessage(String roomUUID, Pageable pageable) {
		return chatMessageRepository.findByChatRoomUUIDOrderBySendAtDesc(roomUUID, pageable)
				.map(ChatMessageResponse::fromEntity)
				.toList();
	}

	/**
	 * 채팅방에 채팅을 발송합니다.
	 *
	 * @param request 채팅 DTO
	 */
	@Transactional
	public void sendChatMessage(ChatMessageRequest request) {
		chatMessageRepository.save(ChatMessage.createMessage(request));
		redisPublisher.publish(request);
	}
}
