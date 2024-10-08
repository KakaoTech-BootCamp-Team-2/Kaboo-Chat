package kaboo.kaboochat.chat.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 회원 Entity
 *
 * @author : parkjihyeok
 * @since : 2024/08/18
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 회원가입 로직은 다른 서비스에서 담당하므로 생성자 접근을 막음, Entity이기 때문에 PROTECTED로 설정
public class Member {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "member_id")
	private Long id;

	@Column(nullable = false, updatable = false)
	private String username;
	@Column(nullable = false, updatable = false)
	private String email;
	@Column(nullable = false, name = "korea_name")
	private String koreaName;
	@Column(nullable = false, name = "english_name")
	private String englishName;
	@Column(nullable = false)
	private String password;

	@Column(columnDefinition = "TEXT")
	private String introduce;

	@Column(name = "class_num")
	private int classNum;
}
