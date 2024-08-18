package kaboo.kaboochat.chat.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import kaboo.kaboochat.chat.domain.entity.ChatMessage;

/**
 * 채팅 메시지를 담당한다.
 *
 * @author : parkjihyeok
 * @since : 2024/08/18
 */
public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {

	/**
	 * 채팅방 메시지를 시간으로 정렬하여 페이지로 가져옵니다.
	 *
	 * @param roomUUID 채팅방 UUID
	 * @param pageable 페이지 정보
	 * @return 해당 페이지
	 */
	Page<ChatMessage> findByChatRoomUUIDOrderBySendAtDesc(String roomUUID, Pageable pageable);
}
