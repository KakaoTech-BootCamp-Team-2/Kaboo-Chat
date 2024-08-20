package kaboo.kaboochat.chat.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import kaboo.kaboochat.chat.domain.entity.Member;

/**
 * 회원을 담당한다.
 *
 * @author : parkjihyeok
 * @since : 2024/08/18
 */
public interface MemberRepository extends JpaRepository<Member, Long> {

	Optional<Member> findByUsername(String username);
}
