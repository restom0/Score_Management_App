package com.vn.golden_owl_backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Score distribution buckets for one subject.")
public record SubjectLevelStatsResponse(
		@Schema(description = "Stable subject code used by the frontend.", example = "math")
		String code,
		@Schema(description = "English subject name.", example = "Math")
		String nameEn,
		@Schema(description = "Vietnamese subject name.", example = "Toan")
		String nameVi,
		@Schema(description = "Number of scores greater than or equal to 8.", example = "124533")
		long excellent,
		@Schema(description = "Number of scores from 6 up to below 8.", example = "314280")
		long good,
		@Schema(description = "Number of scores from 4 up to below 6.", example = "401120")
		long average,
		@Schema(description = "Number of scores below 4.", example = "98231")
		long belowAverage,
		@Schema(description = "Number of imported records without this subject score.", example = "127926")
		long missing
) {
}
