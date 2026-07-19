package com.vn.golden_owl_backend;

import com.vn.golden_owl_backend.model.ScoreRecord;
import com.vn.golden_owl_backend.repository.ScoreRecordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(properties = {
		"app.scores.import.enabled=false",
		"spring.datasource.url=jdbc:h2:mem:golden_owl_test;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1"
})
class GoldenOwlBackendApplicationTests {
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ScoreRecordRepository scoreRecordRepository;

	@BeforeEach
	void setUp() {
		scoreRecordRepository.deleteAll();
		scoreRecordRepository.save(score("01000001", 8.4, 6.0, 5.25, 7.0));
		scoreRecordRepository.save(score("01000002", 9.0, 8.5, 8.75, 6.0));
		scoreRecordRepository.save(score("01000003", 9.4, 8.8, 9.25, 3.0));
	}

	@Test
	void contextLoads() {
		assertNotNull(mockMvc);
	}

	@Test
	void reportsHealth() throws Exception {
		mockMvc.perform(get("/api/health"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.status").value("ok"));
	}

	@Test
	void findsScoreByRegistrationNumber() throws Exception {
		mockMvc.perform(get("/api/scores/01000001"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.registrationNumber").value("01000001"))
				.andExpect(jsonPath("$.groupATotal").value(19.65))
				.andExpect(jsonPath("$.scores[0].code").value("math"))
				.andExpect(jsonPath("$.scores[0].score").value(8.4));
	}

	@Test
	void returnsBadRequestForInvalidRegistrationNumber() throws Exception {
		mockMvc.perform(get("/api/scores/ABC"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.status").value(400))
				.andExpect(jsonPath("$.message").value("Registration number must contain exactly 8 digits"));
	}

	@Test
	void returnsNotFoundForMissingRegistrationNumber() throws Exception {
		mockMvc.perform(get("/api/scores/01999999"))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.status").value(404))
				.andExpect(jsonPath("$.message").value("No score found for registration number: 01999999"));
	}

	@Test
	void reportsScoreLevelsBySubject() throws Exception {
		mockMvc.perform(get("/api/reports/score-levels"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.importedRecords").value(3))
				.andExpect(jsonPath("$.subjects[0].code").value("math"))
				.andExpect(jsonPath("$.subjects[0].excellent").value(3))
				.andExpect(jsonPath("$.subjects[3].good").value(1))
				.andExpect(jsonPath("$.subjects[4].average").value(1));
	}

	@Test
	void reportsTopGroupAStudents() throws Exception {
		mockMvc.perform(get("/api/reports/top-group-a"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].registrationNumber").value("01000003"))
				.andExpect(jsonPath("$[0].total").value(27.45))
				.andExpect(jsonPath("$[1].registrationNumber").value("01000002"));
	}

	@Test
	void clampsTopGroupALimit() throws Exception {
		mockMvc.perform(get("/api/reports/top-group-a?limit=0"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].registrationNumber").value("01000003"))
				.andExpect(jsonPath("$[1]").doesNotExist());
	}

	@Test
	void rejectsInvalidTopGroupALimit() throws Exception {
		mockMvc.perform(get("/api/reports/top-group-a?limit=abc"))
				.andExpect(status().isBadRequest());
	}

	@Test
	void listsSubjects() throws Exception {
		mockMvc.perform(get("/api/subjects"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].code").value("math"))
				.andExpect(jsonPath("$[0].csvColumn").value("toan"))
				.andExpect(jsonPath("$[0].nameEn").value("Math"));
	}

	@Test
	void exposesOpenApiDocument() throws Exception {
		mockMvc.perform(get("/v3/api-docs"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.openapi").exists())
				.andExpect(jsonPath("$.info.title").value("G-Scores API"))
				.andExpect(jsonPath("$.paths['/api/scores/{registrationNumber}']").exists());
	}

	private ScoreRecord score(
			String registrationNumber,
			double math,
			double physics,
			double chemistry,
			double literature
	) {
		ScoreRecord scoreRecord = new ScoreRecord();
		scoreRecord.setRegistrationNumber(registrationNumber);
		scoreRecord.setMath(math);
		scoreRecord.setPhysics(physics);
		scoreRecord.setChemistry(chemistry);
		scoreRecord.setLiterature(literature);
		scoreRecord.setForeignLanguage(7.5);
		scoreRecord.setForeignLanguageCode("N1");
		return scoreRecord;
	}

}
