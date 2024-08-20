package kaboo.kaboochat.chat.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import kaboo.kaboochat.chat.domain.dto.request.ChatMessageRequest;
import kaboo.kaboochat.chat.domain.dto.request.ChatRoomRequest;
import kaboo.kaboochat.chat.domain.dto.response.ChatMessageResponse;
import kaboo.kaboochat.chat.domain.dto.response.ChatRoomResponse;
import kaboo.kaboochat.chat.domain.entity.ChatMessage;
import kaboo.kaboochat.chat.domain.entity.ChatRoom;
import kaboo.kaboochat.chat.service.ChatService;

/**
 * @author : parkjihyeok
 * @since : 2024/08/18
 */
@WebMvcTest(ChatController.class)
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@DisplayName("채팅 Controller 테스트")
class ChatControllerTest {

	@Autowired
	MockMvc mockMvc;
	@MockBean
	ChatService chatService;
	ObjectMapper objectMapper = new ObjectMapper();

	@Test
	@DisplayName("참여중인 채팅방 리스트 조회")
	void findByUsernameTest() throws Exception {
		// Given
		ChatRoom chatRoom1 = ChatRoom.createRoom("채팅방1");
		ChatRoom chatRoom2 = ChatRoom.createRoom("채팅방2");
		ChatRoom chatRoom3 = ChatRoom.createRoom("채팅방3");
		List<ChatRoomResponse> responses = ChatRoomResponse.fromEntityList(List.of(chatRoom1, chatRoom2, chatRoom3));
		given(chatService.findByUsername("pjh5365")).willReturn(responses);

		// When
		mockMvc.perform(get("/chat/rooms")
						.queryParam("username", "pjh5365"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.message").value("요청이 성공적으로 처리되었습니다."))
				.andExpect(jsonPath("$.data").exists())
				.andDo(print())
				.andDo(document("{class-name}/{method-name}/",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						queryParameters(
								parameterWithName("username").description("검색할 username")
						),
						responseFields(
								fieldWithPath("success").description("성공여부"),
								fieldWithPath("message").description("응답 메시지"),
								fieldWithPath("data").description("채팅방 리스트"),
								fieldWithPath("data[].usernames").description("참여자 이름 (null)"),
								fieldWithPath("data[].chatRoomUUID").description("채팅방 UUID"),
								fieldWithPath("data[].chatRoomName").description("채팅방 이름")
						)));

		// Then
	}

	@Test
	@DisplayName("채팅방 생성")
	void createRoomTest() throws Exception {
		// Given
		ChatRoomRequest request = new ChatRoomRequest(List.of("user1", "user2", "user3"), "채팅방");

		// When
		mockMvc.perform(post("/chat/rooms")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isCreated())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.message").value("요청이 성공적으로 처리되었습니다."))
				.andExpect(jsonPath("$.data").value("채팅방 이름: 채팅방"))
				.andDo(print())
				.andDo(document("{class-name}/{method-name}/",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						requestFields(
								fieldWithPath("usernames").description("채팅방 참여자의 ID 리스트"),
								fieldWithPath("chatRoomName").description("생성할 채팅방 이름")
						),
						responseFields(
								fieldWithPath("success").description("성공여부"),
								fieldWithPath("message").description("응답 메시지"),
								fieldWithPath("data").description("채팅방 이름")
						)));

		// Then
	}

	@Test
	@DisplayName("채팅방 생성 실패")
	void createRoomFailTest() throws Exception {
		// Given
		ChatRoomRequest request = new ChatRoomRequest(List.of("user1", "user2", "user3"), "채팅방");
		doThrow(new IllegalArgumentException("해당하는 회원정보를 찾을 수 없습니다.")).when(chatService).createRoom(any());

		// When
		mockMvc.perform(post("/chat/rooms")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isBadRequest())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.message").value("요청에 실패했습니다."))
				.andExpect(jsonPath("$.data").value("해당하는 회원정보를 찾을 수 없습니다."))
				.andDo(print())
				.andDo(document("{class-name}/{method-name}/",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						requestFields(
								fieldWithPath("usernames").description("채팅방 참여자의 ID 리스트"),
								fieldWithPath("chatRoomName").description("생성할 채팅방 이름")
						),
						responseFields(
								fieldWithPath("success").description("성공여부"),
								fieldWithPath("message").description("응답 메시지"),
								fieldWithPath("data").description("실패 내용")
						)));

		// Then
	}

	@Test
	@DisplayName("채팅방의 채팅내역 조회")
	void findChatMessageTest() throws Exception {
		// Given
		ChatMessageRequest request1 = new ChatMessageRequest("A1A1-B2B2", "pjh5365", "justin", "안녕");
		ChatMessageRequest request2 = new ChatMessageRequest("A1A1-B2B2", "pibber", "park", "반가워");
		ChatMessageRequest request3 = new ChatMessageRequest("A1A1-B2B2", "pibber", "park", "거기 날씨 어때?");
		ChatMessageRequest request4 = new ChatMessageRequest("A1A1-B2B2", "pjh5365", "justin", "엄청 더워...");
		ChatMessageRequest request5 = new ChatMessageRequest("A1A1-B2B2", "pjh5365", "justin", "거기는 어때?");
		ChatMessage message1 = ChatMessage.createMessage(request1);
		ChatMessage message2 = ChatMessage.createMessage(request2);
		ChatMessage message3 = ChatMessage.createMessage(request3);
		ChatMessage message4 = ChatMessage.createMessage(request4);
		ChatMessage message5 = ChatMessage.createMessage(request5);
		given(chatService.findMessage(any(), any())).willReturn(List.of(
				ChatMessageResponse.fromEntity(message5),
				ChatMessageResponse.fromEntity(message4),
				ChatMessageResponse.fromEntity(message3),
				ChatMessageResponse.fromEntity(message2),
				ChatMessageResponse.fromEntity(message1)
		));

		// When
		mockMvc.perform(get("/chat/messages")
						.queryParam("roomUUID", "A1A1-B2B2")
						.queryParam("page", "0")
						.queryParam("size", "10"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.message").value("요청이 성공적으로 처리되었습니다."))
				.andExpect(jsonPath("$.data").exists())
				.andDo(print())
				.andDo(document("{class-name}/{method-name}/",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						queryParameters(
								parameterWithName("roomUUID").description("채팅방 UUID"),
								parameterWithName("page").description("현재 페이지 (0부터 시작)"),
								parameterWithName("size").description("한 페이지 크기")
						),
						responseFields(
								fieldWithPath("success").description("성공여부"),
								fieldWithPath("message").description("응답 메시지"),
								fieldWithPath("data[]").description("채팅내역 리스트"),
								fieldWithPath("data[].username").description("참여자 ID"),
								fieldWithPath("data[].nickname").description("참여자 닉네임"),
								fieldWithPath("data[].message").description("채팅 내용"),
								fieldWithPath("data[].sendAt").description("전송 시간")
						)));

		// Then
	}
}
