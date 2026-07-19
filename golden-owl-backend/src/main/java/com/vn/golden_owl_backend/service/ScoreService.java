package com.vn.golden_owl_backend.service;

import com.vn.golden_owl_backend.dto.ScoreLevelReportResponse;
import com.vn.golden_owl_backend.dto.ScoreResponse;
import com.vn.golden_owl_backend.dto.SubjectLevelStatsResponse;
import com.vn.golden_owl_backend.dto.SubjectResponse;
import com.vn.golden_owl_backend.dto.SubjectScoreResponse;
import com.vn.golden_owl_backend.dto.TopGroupAStudentResponse;
import com.vn.golden_owl_backend.exception.ScoreNotFoundException;
import com.vn.golden_owl_backend.model.ScoreLevelStats;
import com.vn.golden_owl_backend.model.ScoreRecord;
import com.vn.golden_owl_backend.model.Subject;
import com.vn.golden_owl_backend.repository.ScoreLevelStatsRepository;
import com.vn.golden_owl_backend.repository.ScoreRecordRepository;
import com.vn.golden_owl_backend.repository.ScoreStatisticsRepository;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ScoreService {
	private final ScoreRecordRepository scoreRecordRepository;
	private final ScoreLevelStatsRepository scoreLevelStatsRepository;
	private final ScoreStatisticsRepository scoreStatisticsRepository;

	public ScoreService(
			ScoreRecordRepository scoreRecordRepository,
			ScoreLevelStatsRepository scoreLevelStatsRepository,
			ScoreStatisticsRepository scoreStatisticsRepository
	) {
		this.scoreRecordRepository = scoreRecordRepository;
		this.scoreLevelStatsRepository = scoreLevelStatsRepository;
		this.scoreStatisticsRepository = scoreStatisticsRepository;
	}

	/**
	 * Looks up one imported student score record and maps every configured subject.
	 */
	public ScoreResponse findScore(String registrationNumber) {
		String normalizedRegistrationNumber = normalizeRegistrationNumber(registrationNumber);
		ScoreRecord record = scoreRecordRepository.findByRegistrationNumber(normalizedRegistrationNumber)
				.orElseThrow(() -> new ScoreNotFoundException(normalizedRegistrationNumber));

		List<SubjectScoreResponse> scores = Arrays.stream(Subject.values())
				.map(subject -> new SubjectScoreResponse(
						subject.getCode(),
						subject.getEnglishName(),
						subject.getVietnameseName(),
						subject.readScore(record)
				))
				.toList();

		return new ScoreResponse(
				record.getRegistrationNumber(),
				record.getForeignLanguageCode(),
				calculateGroupATotal(record),
				scores
		);
	}

	/**
	 * Returns the total imported record count and score level buckets for each subject.
	 */
	public ScoreLevelReportResponse getScoreLevelReport() {
		return new ScoreLevelReportResponse(
				scoreRecordRepository.count(),
				getPersistedStatsOrCalculate()
		);
	}

	/**
	 * Returns top Group A students after clamping the requested result size.
	 */
	public List<TopGroupAStudentResponse> getTopGroupA(int limit) {
		int safeLimit = Math.max(1, Math.min(limit, 100));
		return scoreRecordRepository.findTopGroupA(PageRequest.of(0, safeLimit));
	}

	/**
	 * Lists subject metadata used to label lookup and report responses.
	 */
	public List<SubjectResponse> getSubjects() {
		return Arrays.stream(Subject.values())
				.map(subject -> new SubjectResponse(
						subject.getCode(),
						subject.getCsvColumn(),
						subject.getEnglishName(),
						subject.getVietnameseName()
				))
				.toList();
	}

	/**
	 * Validates and trims a registration number before repository lookup.
	 */
	private String normalizeRegistrationNumber(String registrationNumber) {
		if (registrationNumber == null || registrationNumber.isBlank()) {
			throw new IllegalArgumentException("Registration number is required");
		}
		String normalized = registrationNumber.trim();
		if (!normalized.matches("\\d{8}")) {
			throw new IllegalArgumentException("Registration number must contain exactly 8 digits");
		}
		return normalized;
	}

	/**
	 * Calculates Math, Physics, and Chemistry total when all three scores exist.
	 */
	private Double calculateGroupATotal(ScoreRecord record) {
		if (record.getMath() == null || record.getPhysics() == null || record.getChemistry() == null) {
			return null;
		}
		return record.getMath() + record.getPhysics() + record.getChemistry();
	}

	/**
	 * Uses startup-imported statistics when complete, with query fallback for partial data.
	 */
	private List<SubjectLevelStatsResponse> getPersistedStatsOrCalculate() {
		List<ScoreLevelStats> persistedStats = scoreLevelStatsRepository.findAll();
		if (persistedStats.size() != Subject.values().length) {
			return scoreStatisticsRepository.countAllSubjectsByLevels();
		}

		Map<String, ScoreLevelStats> statsBySubject = persistedStats.stream()
				.collect(Collectors.toMap(ScoreLevelStats::getSubjectCode, Function.identity()));

		return Arrays.stream(Subject.values())
				.map(subject -> toResponse(subject, statsBySubject.get(subject.getCode())))
				.toList();
	}

	/**
	 * Converts one persisted score bucket row to the public report response shape.
	 */
	private SubjectLevelStatsResponse toResponse(Subject subject, ScoreLevelStats stats) {
		if (stats == null) {
			return scoreStatisticsRepository.countAllSubjectsByLevels().stream()
					.filter(response -> response.code().equals(subject.getCode()))
					.findFirst()
					.orElseThrow();
		}
		return new SubjectLevelStatsResponse(
				subject.getCode(),
				subject.getEnglishName(),
				subject.getVietnameseName(),
				stats.getExcellent(),
				stats.getGood(),
				stats.getAverage(),
				stats.getBelowAverage(),
				stats.getMissing()
		);
	}

}
