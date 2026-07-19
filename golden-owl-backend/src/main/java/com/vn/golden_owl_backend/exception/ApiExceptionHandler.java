package com.vn.golden_owl_backend.exception;

import com.vn.golden_owl_backend.dto.ApiErrorResponse;
import java.time.Instant;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {
	@ExceptionHandler(ScoreNotFoundException.class)
	public ResponseEntity<ApiErrorResponse> handleScoreNotFound(ScoreNotFoundException exception) {
		return buildError(HttpStatus.NOT_FOUND, exception.getMessage());
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ApiErrorResponse> handleBadRequest(IllegalArgumentException exception) {
		return buildError(HttpStatus.BAD_REQUEST, exception.getMessage());
	}

	private ResponseEntity<ApiErrorResponse> buildError(HttpStatus status, String message) {
		return ResponseEntity.status(status).body(new ApiErrorResponse(
				Instant.now(),
				status.value(),
				status.getReasonPhrase(),
				message
		));
	}
}
