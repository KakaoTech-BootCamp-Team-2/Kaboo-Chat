package kaboo.kaboochat.chat.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 채팅방 참여자 Entity
 * <p>
 * 이 클래스는 채팅방 참여자를 RDB에 저장하기 위한 Entity입니다.
 * </p>
 *
 * @author : parkjihyeok
 * @since : 2024/08/18
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // createChatMember 메서드로만 메시지 생성, Entity이기 때문에 PROTECTED로 설정
@AllArgsConstructor(access = AccessLevel.PRIVATE) // createChatMember 메서드로만 메시지 생성
@Builder(access = AccessLevel.PRIVATE) // createChatMember 메서드로만 메시지 생성
public class ChatMember {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "chat_member_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "chat_room_id")
	private ChatRoom chatRoom;

	public static ChatMember createChatMember(Member member, ChatRoom chatRoom) {
		return ChatMember.builder()
				.member(member)
				.chatRoom(chatRoom)
				.build();
	}
}
