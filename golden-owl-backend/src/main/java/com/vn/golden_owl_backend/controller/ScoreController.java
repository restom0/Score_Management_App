package com.vn.golden_owl_backend.controller;

import com.vn.golden_owl_backend.dto.ApiErrorResponse;
import com.vn.golden_owl_backend.dto.ScoreLevelReportResponse;
import com.vn.golden_owl_backend.dto.ScoreResponse;
import com.vn.golden_owl_backend.dto.SubjectResponse;
import com.vn.golden_owl_backend.dto.TopGroupAStudentResponse;
import com.vn.golden_owl_backend.service.ScoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@Tag(name = "Scores", description = "Score lookup, report, subject metadata, and health endpoints.")
public class ScoreController {
	private final ScoreService scoreService;

	public ScoreController(ScoreService scoreService) {
		this.scoreService = scoreService;
	}

	/**
	 * Returns a minimal availability marker for uptime checks.
	 */
	@Operation(
			summary = "Check API health",
			description = "Returns a small status payload when the backend is reachable."
	)
	@ApiResponse(
			responseCode = "200",
			description = "Backend is available.",
			content = @Content(
					mediaType = MediaType.APPLICATION_JSON_VALUE,
					examples = @ExampleObject(value = "{\"status\":\"ok\"}")
			)
	)
	@GetMapping("/health")
	public Map<String, String> health() {
		return Map.of("status", "ok");
	}

	/**
	 * Finds one student's subject scores by the 8-digit registration number.
	 */
	@Operation(
			summary = "Find scores by registration number",
			description = "Looks up a single imported score record and returns every subject score plus the Group A total."
	)
	@ApiResponses({
			@ApiResponse(
					responseCode = "200",
					description = "Score record found.",
					content = @Content(
							mediaType = MediaType.APPLICATION_JSON_VALUE,
							schema = @Schema(implementation = ScoreResponse.class)
					)
			),
			@ApiResponse(
					responseCode = "400",
					description = "Registration number is blank or not exactly 8 digits.",
					content = @Content(
							mediaType = MediaType.APPLICATION_JSON_VALUE,
							schema = @Schema(implementation = ApiErrorResponse.class)
					)
			),
			@ApiResponse(
					responseCode = "404",
					description = "No score record exists for the registration number.",
					content = @Content(
							mediaType = MediaType.APPLICATION_JSON_VALUE,
							schema = @Schema(implementation = ApiErrorResponse.class)
					)
			)
	})
	@GetMapping("/scores/{registrationNumber}")
	public ScoreResponse findScore(
			@Parameter(
					description = "Student registration number. Must contain exactly 8 digits.",
					example = "01000001",
					required = true
			)
			@PathVariable String registrationNumber
	) {
		return scoreService.findScore(registrationNumber);
	}

	/**
	 * Returns score distribution buckets for every supported subject.
	 */
	@Operation(
			summary = "Get score level report",
			description = "Returns imported record count and per-subject counts for >= 8, 6 - 8, 4 - 6, < 4, and missing scores."
	)
	@ApiResponse(
			responseCode = "200",
			description = "Score level report generated.",
			content = @Content(
					mediaType = MediaType.APPLICATION_JSON_VALUE,
					schema = @Schema(implementation = ScoreLevelReportResponse.class)
			)
	)
	@GetMapping("/reports/score-levels")
	public ScoreLevelReportResponse scoreLevels() {
		return scoreService.getScoreLevelReport();
	}

	/**
	 * Returns the best Group A students by Math, Physics, and Chemistry total.
	 */
	@Operation(
			summary = "Get top Group A students",
			description = "Returns students ranked by Math + Physics + Chemistry total. The limit is clamped to the 1 - 100 range."
	)
	@ApiResponses({
			@ApiResponse(
					responseCode = "200",
					description = "Top Group A students returned.",
					content = @Content(
							mediaType = MediaType.APPLICATION_JSON_VALUE,
							array = @ArraySchema(schema = @Schema(implementation = TopGroupAStudentResponse.class))
					)
			),
			@ApiResponse(
					responseCode = "400",
					description = "Limit is not a valid integer.",
					content = @Content(
							mediaType = MediaType.APPLICATION_JSON_VALUE,
							schema = @Schema(implementation = ApiErrorResponse.class)
					)
			)
	})
	@GetMapping("/reports/top-group-a")
	public List<TopGroupAStudentResponse> topGroupA(
			@Parameter(
					description = "Maximum number of students to return. Values below 1 become 1, values above 100 become 100.",
					example = "10"
			)
			@RequestParam(defaultValue = "10") int limit
	) {
		return scoreService.getTopGroupA(limit);
	}

	/**
	 * Lists subjects that can appear in score lookup and report payloads.
	 */
	@Operation(
			summary = "List subjects",
			description = "Returns the supported subject codes, CSV columns, and bilingual display names."
	)
	@ApiResponse(
			responseCode = "200",
			description = "Subject metadata returned.",
			content = @Content(
					mediaType = MediaType.APPLICATION_JSON_VALUE,
					array = @ArraySchema(schema = @Schema(implementation = SubjectResponse.class))
			)
	)
	@GetMapping("/subjects")
	public List<SubjectResponse> subjects() {
		return scoreService.getSubjects();
	}
}
