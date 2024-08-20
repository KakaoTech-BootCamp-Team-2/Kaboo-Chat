package kaboo.kaboochat.chat.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

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
@DisplayName("채팅 참여자 Repository 테스트")
@Transactional
class ChatMemberRepositoryTest {

	@Autowired
	DataSource dataSource;
	@Autowired
	MemberRepository memberRepository;
	@Autowired
	ChatMemberRepository chatMemberRepository;
	@Autowired
	ChatRoomRepository chatRoomRepository;
	ChatRoom chatRoom;

	@BeforeEach
	void setUp() {
		chatRoom = ChatRoom.createRoom("채팅방1");
		chatRoomRepository.save(chatRoom);
		JdbcTemplate jdbc = new JdbcTemplate(dataSource);
		String sql1 = "insert into member(username, nickname, password) values ('pjh1', '1111', 'pw111')";
		String sql2 = "insert into member(username, nickname, password) values ('pjh2', '2222', 'pw222')";
		String sql3 = "insert into member(username, nickname, password) values ('pjh3', '3333', 'pw333')";
		String sql4 = "insert into member(username, nickname, password) values ('pjh4', '4444', 'pw444')";
		jdbc.execute(sql1);
		jdbc.execute(sql2);
		jdbc.execute(sql3);
		jdbc.execute(sql4);

		Member member1 = memberRepository.findByUsername("pjh1").get();
		Member member2 = memberRepository.findByUsername("pjh2").get();
		Member member3 = memberRepository.findByUsername("pjh3").get();
		Member member4 = memberRepository.findByUsername("pjh4").get();

		chatMemberRepository.save(ChatMember.createChatMember(member1, chatRoom));
		chatMemberRepository.save(ChatMember.createChatMember(member2, chatRoom));
		chatMemberRepository.save(ChatMember.createChatMember(member3, chatRoom));
		chatMemberRepository.save(ChatMember.createChatMember(member4, chatRoom));
	}

	@Test
	@DisplayName("채팅방 UUID로 채팅방 참여자들 찾기")
	void findByChatUUIDTest() {
	    // Given
		String UUID = chatRoom.getChatRoomUUID();

	    // When
		List<ChatMember> result = chatMemberRepository.findByChatRoomUUID(UUID);

		// Then
		assertEquals(4, result.size());
		assertEquals("pjh3", result.get(2).getMember().getUsername());
		assertEquals("2222", result.get(1).getMember().getNickname());
		assertEquals("pw444", result.get(3).getMember().getPassword());
	}
}
