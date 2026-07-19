package com.vn.golden_owl_backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "One subject score inside a registration lookup response.")
public record SubjectScoreResponse(
		@Schema(description = "Stable subject code used by the API and frontend.", example = "math")
		String code,
		@Schema(description = "English subject name.", example = "Math")
		String nameEn,
		@Schema(description = "Vietnamese subject name.", example = "Toan")
		String nameVi,
		@Schema(description = "Score for the subject. Null means the student did not have this score in the CSV.", example = "8.4", nullable = true)
		Double score
) {
}
