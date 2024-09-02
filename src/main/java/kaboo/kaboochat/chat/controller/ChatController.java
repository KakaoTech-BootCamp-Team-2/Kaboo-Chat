package kaboo.kaboochat.chat.controller;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kaboo.kaboochat.chat.domain.dto.request.ChatMessageRequest;
import kaboo.kaboochat.chat.domain.dto.request.ChatRoomRequest;
import kaboo.kaboochat.chat.domain.dto.response.ApiResponse;
import kaboo.kaboochat.chat.domain.dto.response.ChatMessageResponse;
import kaboo.kaboochat.chat.domain.dto.response.ChatRoomResponse;
import kaboo.kaboochat.chat.service.ChatService;
import lombok.RequiredArgsConstructor;

/**
 * 채팅을 담당하는 Controller
 *
 * @author : parkjihyeok
 * @since : 2024/08/18
 */
@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

	private final ChatService chatService;

	/**
	 * username으로 참여중인 채팅방 리스트를 불러옵니다.
	 *
	 * @param username 사용자 ID
	 * @return 참여중인 채팅방 리스트
	 */
	@GetMapping("/rooms")
	public ResponseEntity<ApiResponse<List<ChatRoomResponse>>> findChatRoomListByUsernameChat(String username) {
		List<ChatRoomResponse> responses = chatService.findByUsername(username);
		return ResponseEntity.status(HttpStatus.OK)
				.body(ApiResponse.success(responses));
	}

	/**
	 * 채팅방의 상세 정보를 불러옵니다.
	 *
	 * @param roomUUID 채팅방 UUID
	 * @return 채팅방 상세 정보
	 */
	@GetMapping("/rooms/details")
	public ResponseEntity<ApiResponse<ChatRoomResponse>> findChatRoomDetails(String roomUUID) {
		ChatRoomResponse response = chatService.findByChatUUID(roomUUID);

		return ResponseEntity.status(HttpStatus.OK)
				.body(ApiResponse.success(response));
	}

	/**
	 * 채팅방을 생성합니다.
	 *
	 * @param chatRoomRequest 채팅방 생성에 필요한 DTO
	 * @return 처리 결과
	 */
	@PostMapping("/rooms")
	public ResponseEntity<ApiResponse<String>> createRoom(@RequestBody ChatRoomRequest chatRoomRequest) {
		chatService.createRoom(chatRoomRequest);

		return ResponseEntity.status(HttpStatus.CREATED)
				.body(ApiResponse.success("채팅방 이름: " + chatRoomRequest.getChatRoomName()));
	}

	/**
	 * 채팅방의 채팅 내역을 불러옵니다.
	 *
	 * @param roomUUID 채팅방 UUID
	 * @param pageable 페이지 정보
	 * @return 채팅내역
	 */
	@GetMapping("/messages")
	public ResponseEntity<ApiResponse<List<ChatMessageResponse>>> findMessages(String roomUUID, Pageable pageable) {
		List<ChatMessageResponse> message = chatService.findMessage(roomUUID, pageable);
		return ResponseEntity.status(HttpStatus.OK)
				.body(ApiResponse.success(message));
	}

	/**
	 * 웹소켓의 메세지를 처리합니다.
	 *
	 * @param message 메시지
	 */
	@MessageMapping("/messages")
	public void sendMessage(ChatMessageRequest message) {
		chatService.sendChatMessage(message);
	}
}
