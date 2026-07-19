package com.vn.golden_owl_backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

@Schema(description = "Standard API error response.")
public record ApiErrorResponse(
		@Schema(description = "Time when the error was produced.", example = "2026-07-19T10:15:30Z")
		Instant timestamp,
		@Schema(description = "HTTP status code.", example = "404")
		int status,
		@Schema(description = "HTTP reason phrase.", example = "Not Found")
		String error,
		@Schema(description = "Human-readable error detail.", example = "Score not found for registration number: 01000001")
		String message
) {
}
