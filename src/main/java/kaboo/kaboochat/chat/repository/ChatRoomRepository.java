package kaboo.kaboochat.chat.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import kaboo.kaboochat.chat.domain.entity.ChatRoom;

/**
 * 채팅방을 담당한다.
 *
 * @author : parkjihyeok
 * @since : 2024/08/18
 */
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

	@Query("select cr from ChatRoom cr where cr.chatRoomUUID = :roomUUID")
	Optional<ChatRoom> findByRoomUUID(String roomUUID);

	/**
	 * username으로 참여자가 참여중인 채팅방 리스트 찾기
	 */
	@Query("select cm.chatRoom from ChatMember cm where cm.member.username = :username")
	List<ChatRoom> findByUsername(String username);
}
