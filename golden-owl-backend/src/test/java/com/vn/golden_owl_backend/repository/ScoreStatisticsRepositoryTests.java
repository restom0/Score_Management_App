package com.vn.golden_owl_backend.repository;

import com.vn.golden_owl_backend.dto.SubjectLevelStatsResponse;
import com.vn.golden_owl_backend.model.ScoreRecord;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = {
		"spring.datasource.url=jdbc:h2:mem:golden_owl_stats_test;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1"
})
@Import(ScoreStatisticsRepository.class)
class ScoreStatisticsRepositoryTests {
	@Autowired
	private ScoreRecordRepository scoreRecordRepository;

	@Autowired
	private ScoreStatisticsRepository scoreStatisticsRepository;

	@Test
	void countsZeroBucketsWhenThereAreNoScoreRecords() {
		List<SubjectLevelStatsResponse> stats = scoreStatisticsRepository.countAllSubjectsByLevels();

		assertThat(stats).hasSize(9);
		assertThat(stats.get(0).excellent()).isZero();
		assertThat(stats.get(0).missing()).isZero();
	}

	@Test
	void countsScoreBucketsAndMissingScoresBySubject() {
		scoreRecordRepository.save(score("01000001", 8.0, 8.5));
		scoreRecordRepository.save(score("01000002", 7.0, null));
		scoreRecordRepository.save(score("01000003", 5.0, null));
		scoreRecordRepository.save(score("01000004", 3.0, null));
		scoreRecordRepository.save(score("01000005", null, null));

		List<SubjectLevelStatsResponse> stats = scoreStatisticsRepository.countAllSubjectsByLevels();

		assertThat(stats.get(0).code()).isEqualTo("math");
		assertThat(stats.get(0).excellent()).isEqualTo(1);
		assertThat(stats.get(0).good()).isEqualTo(1);
		assertThat(stats.get(0).average()).isEqualTo(1);
		assertThat(stats.get(0).belowAverage()).isEqualTo(1);
		assertThat(stats.get(0).missing()).isEqualTo(1);
		assertThat(stats.get(3).code()).isEqualTo("physics");
		assertThat(stats.get(3).excellent()).isEqualTo(1);
		assertThat(stats.get(3).missing()).isEqualTo(4);
	}

	private ScoreRecord score(String registrationNumber, Double math, Double physics) {
		ScoreRecord scoreRecord = new ScoreRecord();
		scoreRecord.setRegistrationNumber(registrationNumber);
		scoreRecord.setMath(math);
		scoreRecord.setPhysics(physics);
		return scoreRecord;
	}
}
