package com.vn.golden_owl_backend;

import com.vn.golden_owl_backend.repository.ScoreRecordRepository;
import com.vn.golden_owl_backend.repository.ScoreLevelStatsRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {
		"app.scores.import.enabled=true",
		"app.scores.import.csv-path=src/test/resources/sample_scores.csv",
		"spring.datasource.url=jdbc:h2:mem:golden_owl_import_test;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1"
})
class CsvScoreImporterTests {
	@Autowired
	private ScoreRecordRepository scoreRecordRepository;

	@Autowired
	private ScoreLevelStatsRepository scoreLevelStatsRepository;

	@Test
	void importsCsvRowsOnStartup() {
		assertThat(scoreRecordRepository.count()).isEqualTo(2);
		assertThat(scoreLevelStatsRepository.count()).isEqualTo(9);
		assertThat(scoreRecordRepository.findByRegistrationNumber("01000011"))
				.isPresent()
				.get()
				.satisfies(record -> {
					assertThat(record.getMath()).isEqualTo(8.4);
					assertThat(record.getPhysics()).isEqualTo(6.0);
					assertThat(record.getGeography()).isNull();
					assertThat(record.getForeignLanguageCode()).isEqualTo("N1");
				});
	}
}
