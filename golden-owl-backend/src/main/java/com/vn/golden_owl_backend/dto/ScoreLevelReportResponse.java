package com.vn.golden_owl_backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Score distribution report across all subjects.")
public record ScoreLevelReportResponse(
		@Schema(description = "Total score rows imported into the database.", example = "1066090")
		long importedRecords,
		@Schema(description = "Distribution buckets for each supported subject.")
		List<SubjectLevelStatsResponse> subjects
) {
}
