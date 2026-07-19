package com.vn.golden_owl_backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Full score lookup response for one registration number.")
public record ScoreResponse(
		@Schema(description = "Student registration number.", example = "01000001")
		String registrationNumber,
		@Schema(description = "Foreign language code from the source CSV, when present.", example = "N1", nullable = true)
		String foreignLanguageCode,
		@Schema(description = "Math + Physics + Chemistry total. Null when one of the subjects is missing.", example = "24.75", nullable = true)
		Double groupATotal,
		@Schema(description = "All supported subjects and this student's score for each subject.")
		List<SubjectScoreResponse> scores
) {
	public ScoreResponse {
		groupATotal = round(groupATotal);
	}

	private static Double round(Double value) {
		return value == null ? null : Math.round(value * 100.0) / 100.0;
	}
}
