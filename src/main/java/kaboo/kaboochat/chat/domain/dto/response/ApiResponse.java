package kaboo.kaboochat.chat.domain.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * API 응답 DTO
 * <p>
 * 이 클래스는 모든 API의 응답을 감싸기 위한 클래스입니다.
 * </p>
 *
 * @author : parkjihyeok
 * @since : 2024/08/18
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiResponse<T> {

	private boolean success; // 응답 성공 여부
	private String message; // 응답 메시지에 대한 정보
	private T data; // 응답객체를 공통으로 사용하기 위해 제네릭으로 받아 처리하기

	public static <T> ApiResponse<T> success(T data) {
		return new ApiResponse<>(true, "요청이 성공적으로 처리되었습니다.", data);
	}

	public static <T> ApiResponse<T> success(String message, T data) {
		return new ApiResponse<>(true, message, data);
	}

	public static <T> ApiResponse<T> failure(T data) {
		return new ApiResponse<>(false, "요청에 실패했습니다.", data);
	}

	public static <T> ApiResponse<T> failure(String message, T data) {
		return new ApiResponse<>(false, message, data);
	}
}
