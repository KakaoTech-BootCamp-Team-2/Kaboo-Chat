package kaboo.kaboochat.chat.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import kaboo.kaboochat.chat.domain.dto.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * 컨트롤러단 예외처리 클래스
 *
 * @author : parkjihyeok
 * @since : 2024/08/18
 */
@Slf4j
@RestControllerAdvice
public class ControllerAdvice {

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiResponse<String>> common(Exception e) {
		log.error("[Kaboo-Chat]: 예상치 못한 예외가 발생하였습니다. 예외내용 = {}", e.getMessage());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(ApiResponse.failure("관리자에게 문의해주세요."));
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ApiResponse<String>> illegalArgument(Exception e) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(ApiResponse.failure(e.getMessage()));
	}
}
