package com.vn.golden_owl_backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Subject metadata used by reports and lookup responses.")
public record SubjectResponse(
		@Schema(description = "Stable subject code used by the API and frontend.", example = "math")
		String code,
		@Schema(description = "CSV column name in the source score file.", example = "toan")
		String csvColumn,
		@Schema(description = "English display name.", example = "Math")
		String nameEn,
		@Schema(description = "Vietnamese display name.", example = "Toan")
		String nameVi
) {
}
