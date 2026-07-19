package com.vn.golden_owl_backend.service;

import com.vn.golden_owl_backend.dto.ScoreLevelReportResponse;
import com.vn.golden_owl_backend.dto.ScoreResponse;
import com.vn.golden_owl_backend.dto.SubjectLevelStatsResponse;
import com.vn.golden_owl_backend.exception.ScoreNotFoundException;
import com.vn.golden_owl_backend.model.ScoreLevelStats;
import com.vn.golden_owl_backend.model.ScoreRecord;
import com.vn.golden_owl_backend.model.Subject;
import com.vn.golden_owl_backend.repository.ScoreLevelStatsRepository;
import com.vn.golden_owl_backend.repository.ScoreRecordRepository;
import com.vn.golden_owl_backend.repository.ScoreStatisticsRepository;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ScoreServiceTests {
	@Mock
	private ScoreRecordRepository scoreRecordRepository;

	@Mock
	private ScoreLevelStatsRepository scoreLevelStatsRepository;

	@Mock
	private ScoreStatisticsRepository scoreStatisticsRepository;

	@InjectMocks
	private ScoreService scoreService;

	@Test
	void findsScoreAndCalculatesGroupATotal() {
		ScoreRecord scoreRecord = score("01000001", 8.4, 6.0, 5.25);
		scoreRecord.setLiterature(7.0);
		scoreRecord.setForeignLanguage(8.0);
		scoreRecord.setBiology(4.5);
		scoreRecord.setHistory(3.25);
		scoreRecord.setGeography(6.75);
		scoreRecord.setCivicEducation(9.0);
		scoreRecord.setForeignLanguageCode("N1");
		when(scoreRecordRepository.findByRegistrationNumber("01000001")).thenReturn(Optional.of(scoreRecord));

		ScoreResponse response = scoreService.findScore(" 01000001 ");

		assertThat(response.registrationNumber()).isEqualTo("01000001");
		assertThat(response.foreignLanguageCode()).isEqualTo("N1");
		assertThat(response.groupATotal()).isEqualTo(19.65);
		assertThat(response.scores()).hasSize(Subject.values().length);
		assertThat(response.scores().get(0).code()).isEqualTo("math");
		assertThat(response.scores().get(8).score()).isEqualTo(9.0);
	}

	@Test
	void returnsNullGroupATotalWhenAnyGroupAScoreIsMissing() {
		ScoreRecord scoreRecord = score("01000002", 8.4, 6.0, null);
		when(scoreRecordRepository.findByRegistrationNumber("01000002")).thenReturn(Optional.of(scoreRecord));

		ScoreResponse response = scoreService.findScore("01000002");

		assertThat(response.groupATotal()).isNull();
	}

	@ParameterizedTest
	@NullAndEmptySource
	@ValueSource(strings = {" ", "1234567", "123456789", "ABCDEFGH"})
	void rejectsMissingOrInvalidRegistrationNumbers(String registrationNumber) {
		assertThatThrownBy(() -> scoreService.findScore(registrationNumber))
				.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void throwsWhenScoreDoesNotExist() {
		when(scoreRecordRepository.findByRegistrationNumber("01009999")).thenReturn(Optional.empty());

		assertThatThrownBy(() -> scoreService.findScore("01009999"))
				.isInstanceOf(ScoreNotFoundException.class)
				.hasMessageContaining("01009999");
	}

	@Test
	void returnsPersistedStatsWhenEverySubjectIsPresent() {
		when(scoreRecordRepository.count()).thenReturn(42L);
		when(scoreLevelStatsRepository.findAll()).thenReturn(Arrays.stream(Subject.values())
				.map(subject -> stats(subject.getCode(), subject.ordinal()))
				.toList());

		ScoreLevelReportResponse response = scoreService.getScoreLevelReport();

		assertThat(response.importedRecords()).isEqualTo(42);
		assertThat(response.subjects()).hasSize(Subject.values().length);
		assertThat(response.subjects().get(0).excellent()).isEqualTo(10);
		verify(scoreStatisticsRepository, never()).countAllSubjectsByLevels();
	}

	@Test
	void fallsBackWhenPersistedStatsAreIncomplete() {
		List<SubjectLevelStatsResponse> fallbackStats = List.of(fallbackStats(Subject.MATH, 5));
		when(scoreRecordRepository.count()).thenReturn(7L);
		when(scoreLevelStatsRepository.findAll()).thenReturn(List.of(stats(Subject.MATH.getCode(), 0)));
		when(scoreStatisticsRepository.countAllSubjectsByLevels()).thenReturn(fallbackStats);

		ScoreLevelReportResponse response = scoreService.getScoreLevelReport();

		assertThat(response.importedRecords()).isEqualTo(7);
		assertThat(response.subjects()).isSameAs(fallbackStats);
	}

	@Test
	void fallsBackForMissingSubjectWhenPersistedStatsSizeLooksComplete() {
		List<ScoreLevelStats> persistedStats = Arrays.stream(Subject.values())
				.filter(subject -> subject != Subject.MATH)
				.map(subject -> stats(subject.getCode(), subject.ordinal()))
				.toList();
		List<ScoreLevelStats> mismatchedStats = new java.util.ArrayList<>(persistedStats);
		mismatchedStats.add(stats("custom", 99));
		when(scoreRecordRepository.count()).thenReturn(9L);
		when(scoreLevelStatsRepository.findAll()).thenReturn(mismatchedStats);
		when(scoreStatisticsRepository.countAllSubjectsByLevels())
				.thenReturn(List.of(fallbackStats(Subject.MATH, 77)));

		ScoreLevelReportResponse response = scoreService.getScoreLevelReport();

		assertThat(response.importedRecords()).isEqualTo(9);
		assertThat(response.subjects().get(0).code()).isEqualTo("math");
		assertThat(response.subjects().get(0).excellent()).isEqualTo(77);
		verify(scoreStatisticsRepository).countAllSubjectsByLevels();
	}

	@Test
	void clampsTopGroupALimit() {
		when(scoreRecordRepository.findTopGroupA(any(Pageable.class))).thenReturn(List.of());

		scoreService.getTopGroupA(0);
		scoreService.getTopGroupA(10);
		scoreService.getTopGroupA(150);

		ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
		verify(scoreRecordRepository, times(3)).findTopGroupA(pageableCaptor.capture());
		assertThat(pageableCaptor.getAllValues())
				.extracting(Pageable::getPageSize)
				.containsExactly(1, 10, 100);
	}

	@Test
	void listsSupportedSubjects() {
		assertThat(scoreService.getSubjects())
				.hasSize(Subject.values().length)
				.first()
				.satisfies(subject -> {
					assertThat(subject.code()).isEqualTo("math");
					assertThat(subject.csvColumn()).isEqualTo("toan");
					assertThat(subject.nameEn()).isEqualTo("Math");
				});
	}

	private ScoreRecord score(String registrationNumber, Double math, Double physics, Double chemistry) {
		ScoreRecord scoreRecord = new ScoreRecord();
		scoreRecord.setRegistrationNumber(registrationNumber);
		scoreRecord.setMath(math);
		scoreRecord.setPhysics(physics);
		scoreRecord.setChemistry(chemistry);
		return scoreRecord;
	}

	private ScoreLevelStats stats(String subjectCode, long offset) {
		ScoreLevelStats stats = new ScoreLevelStats();
		stats.setSubjectCode(subjectCode);
		stats.setExcellent(10 + offset);
		stats.setGood(20 + offset);
		stats.setAverage(30 + offset);
		stats.setBelowAverage(40 + offset);
		stats.setMissing(50 + offset);
		return stats;
	}

	private SubjectLevelStatsResponse fallbackStats(Subject subject, long excellent) {
		return new SubjectLevelStatsResponse(
				subject.getCode(),
				subject.getEnglishName(),
				subject.getVietnameseName(),
				excellent,
				0,
				0,
				0,
				0
		);
	}
}
