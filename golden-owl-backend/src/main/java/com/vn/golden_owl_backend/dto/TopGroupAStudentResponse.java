package com.vn.golden_owl_backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Ranked Group A student score summary.")
public record TopGroupAStudentResponse(
		@Schema(description = "Student registration number.", example = "01000001")
		String registrationNumber,
		@Schema(description = "Math score.", example = "9.4")
		Double math,
		@Schema(description = "Physics score.", example = "9.75")
		Double physics,
		@Schema(description = "Chemistry score.", example = "9.25")
		Double chemistry,
		@Schema(description = "Math + Physics + Chemistry total.", example = "28.4")
		Double total
) {
	public TopGroupAStudentResponse {
		math = round(math);
		physics = round(physics);
		chemistry = round(chemistry);
		total = round(total);
	}

	private static Double round(Double value) {
		return value == null ? null : Math.round(value * 100.0) / 100.0;
	}
}
