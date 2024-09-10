package kaboo.kaboochat.chat.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import kaboo.kaboochat.chat.domain.entity.ChatMember;
import kaboo.kaboochat.chat.domain.entity.ChatRoom;
import kaboo.kaboochat.chat.domain.entity.Member;

/**
 * @author : parkjihyeok
 * @since : 2024/08/18
 */
@ActiveProfiles("test")
@DataJpaTest
@DisplayName("채팅방 Repository 테스트")
@Transactional
class ChatRoomRepositoryTest {

	@Autowired
	DataSource dataSource;
	@Autowired
	private MemberRepository memberRepository;
	@Autowired
	private ChatRoomRepository chatRoomRepository;
	@Autowired
	private ChatMemberRepository chatMemberRepository;
	ChatRoom chatRoom;

	@BeforeEach
	void setUp() {
		chatRoom = ChatRoom.createRoom("채팅방1");
		chatRoomRepository.save(chatRoom);
		JdbcTemplate jdbc = new JdbcTemplate(dataSource);
		String sql1 = "insert into member (username, email, korea_name, english_name, password, introduce, class_num) values ('pjh1', 'pjh1@example.com', '1111', 'User One', 'password1', '안녕하세요. 유저1입니다.', 101)";
		String sql2 = "insert into member (username, email, korea_name, english_name, password, introduce, class_num) values ('pjh2', 'pjh2@example.com', '2222', 'User Two', 'password2', '안녕하세요. 유저2입니다.', 102)";
		String sql3 = "insert into member (username, email, korea_name, english_name, password, introduce, class_num) values ('pjh3', 'pjh3@example.com', '3333', 'User Three', 'password3', '안녕하세요. 유저3입니다.', 103)";
		String sql4 = "insert into member (username, email, korea_name, english_name, password, introduce, class_num) values ('pjh4', 'pjh4@example.com', '4444', 'User Four', 'password4', '안녕하세요. 유저4입니다.', 104)";
		jdbc.execute(sql1);
		jdbc.execute(sql2);
		jdbc.execute(sql3);
		jdbc.execute(sql4);
	}

	@Test
	@DisplayName("UUID값으로 채팅방을 찾아오는 테스트")
	void findByRoomUUIDTest() {
		// Given

		// When
		Optional<ChatRoom> result = chatRoomRepository.findByRoomUUID(chatRoom.getChatRoomUUID());

		// Then
		assertNotEquals(Optional.empty(), result);
		assertEquals("채팅방1", result.get().getChatRoomName());
	}

	@Test
	@DisplayName("참여자로 채팅방을 찾아오는 테스트")
	void findByUsernameTest1() {
		// Given
		ChatRoom c2 = ChatRoom.createRoom("채팅방2");
		ChatRoom c3 = ChatRoom.createRoom("채팅방3");
		ChatRoom c4 = ChatRoom.createRoom("채팅방4");
		chatRoomRepository.save(c2);
		chatRoomRepository.save(c3);
		chatRoomRepository.save(c4);

		Member member1 = memberRepository.findByUsername("pjh1").get();
		chatMemberRepository.save(ChatMember.createChatMember(member1, chatRoom));
		chatMemberRepository.save(ChatMember.createChatMember(member1, c2));
		chatMemberRepository.save(ChatMember.createChatMember(member1, c3));
		chatMemberRepository.save(ChatMember.createChatMember(member1, c4));

		// When
		List<ChatRoom> result = chatRoomRepository.findByUsername("pjh1");

		// Then
		assertEquals(4, result.size());
		assertEquals("채팅방2", result.get(1).getChatRoomName());
	}

	@Test
	@DisplayName("참여자로 채팅방을 찾아오는 테스트 (채팅방 없음)")
	void findByUsernameTest2() {
		// Given

		// When
		List<ChatRoom> result = chatRoomRepository.findByUsername("pjh1");

		// Then
		assertEquals(0, result.size());
	}
}
