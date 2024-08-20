package kaboo.kaboochat.chat.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import kaboo.kaboochat.chat.domain.entity.ChatMember;

/**
 * 채팅방 참여자를 담당한다.
 *
 * @author : parkjihyeok
 * @since : 2024/08/18
 */
public interface ChatMemberRepository extends JpaRepository<ChatMember, Long> {

	/**
	 * 채팅방 UUID로 채팅방 참여자들 찾기
	 */
	@Query("select cm from ChatMember cm join fetch cm.member m where cm.chatRoom.chatRoomUUID = :roomUUID")
	List<ChatMember> findByChatRoomUUID(String roomUUID);
}
