package com.vn.golden_owl_backend.seeder;

import com.vn.golden_owl_backend.model.ScoreRecord;
import com.vn.golden_owl_backend.model.ScoreLevelStats;
import com.vn.golden_owl_backend.model.Subject;
import com.vn.golden_owl_backend.repository.ScoreLevelStatsRepository;
import com.vn.golden_owl_backend.repository.ScoreRecordRepository;
import com.vn.golden_owl_backend.repository.ScoreStatisticsRepository;
import jakarta.persistence.EntityManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CsvScoreImporter implements ApplicationRunner {
	private static final Logger LOGGER = LoggerFactory.getLogger(CsvScoreImporter.class);
	private static final int EXPECTED_COLUMN_COUNT = 11;

	private final ScoreRecordRepository scoreRecordRepository;
	private final ScoreLevelStatsRepository scoreLevelStatsRepository;
	private final ScoreStatisticsRepository scoreStatisticsRepository;
	private final EntityManager entityManager;
	private final boolean importEnabled;
	private final String csvPath;
	private final int batchSize;

	public CsvScoreImporter(
			ScoreRecordRepository scoreRecordRepository,
			ScoreLevelStatsRepository scoreLevelStatsRepository,
			ScoreStatisticsRepository scoreStatisticsRepository,
			EntityManager entityManager,
			@Value("${app.scores.import.enabled}") boolean importEnabled,
			@Value("${app.scores.import.csv-path}") String csvPath,
			@Value("${app.scores.import.batch-size}") int batchSize
	) {
		this.scoreRecordRepository = scoreRecordRepository;
		this.scoreLevelStatsRepository = scoreLevelStatsRepository;
		this.scoreStatisticsRepository = scoreStatisticsRepository;
		this.entityManager = entityManager;
		this.importEnabled = importEnabled;
		this.csvPath = csvPath;
		this.batchSize = batchSize;
	}

	@Override
	@Transactional
	public void run(ApplicationArguments args) throws IOException {
		if (!importEnabled) {
			LOGGER.info("Score CSV import disabled");
			return;
		}
		if (scoreRecordRepository.count() > 0) {
			warmStatsFromExistingDataIfNeeded();
			LOGGER.info("Score table already has data; skip CSV import");
			return;
		}

		Path path = Path.of(csvPath);
		if (!Files.exists(path)) {
			throw new IllegalStateException("Score CSV file not found: " + path.toAbsolutePath());
		}

		long imported = importCsv(path);
		LOGGER.info("Imported {} score records from {}", imported, path.toAbsolutePath());
	}

	private long importCsv(Path path) throws IOException {
		long imported = 0;
		int rowNumber = 1;
		List<ScoreRecord> batch = new ArrayList<>(batchSize);
		Map<Subject, LevelCounter> counters = createCounters();

		try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
			String header = reader.readLine();
			validateHeader(header);

			String line;
			while ((line = reader.readLine()) != null) {
				rowNumber++;
				if (line.isBlank()) {
					continue;
				}

				ScoreRecord record = parseRecord(line, rowNumber);
				countRecord(record, counters);
				batch.add(record);
				if (batch.size() >= batchSize) {
					persistBatch(batch);
					imported += batch.size();
					batch.clear();
				}
			}
		}

		if (!batch.isEmpty()) {
			persistBatch(batch);
			imported += batch.size();
		}
		persistStats(counters);

		return imported;
	}

	private void validateHeader(String header) {
		String expectedHeader = "sbd,toan,ngu_van,ngoai_ngu,vat_li,hoa_hoc,sinh_hoc,lich_su,dia_li,gdcd,ma_ngoai_ngu";
		if (!expectedHeader.equals(header)) {
			throw new IllegalArgumentException("Unexpected CSV header. Expected: " + expectedHeader);
		}
	}

	private ScoreRecord parseRecord(String line, int rowNumber) {
		String[] columns = line.split(",", -1);
		if (columns.length != EXPECTED_COLUMN_COUNT) {
			throw new IllegalArgumentException("Invalid CSV column count at row " + rowNumber);
		}

		ScoreRecord record = new ScoreRecord();
		record.setRegistrationNumber(parseRegistrationNumber(columns[0], rowNumber));
		record.setMath(parseScore(columns[1], "toan", rowNumber));
		record.setLiterature(parseScore(columns[2], "ngu_van", rowNumber));
		record.setForeignLanguage(parseScore(columns[3], "ngoai_ngu", rowNumber));
		record.setPhysics(parseScore(columns[4], "vat_li", rowNumber));
		record.setChemistry(parseScore(columns[5], "hoa_hoc", rowNumber));
		record.setBiology(parseScore(columns[6], "sinh_hoc", rowNumber));
		record.setHistory(parseScore(columns[7], "lich_su", rowNumber));
		record.setGeography(parseScore(columns[8], "dia_li", rowNumber));
		record.setCivicEducation(parseScore(columns[9], "gdcd", rowNumber));
		record.setForeignLanguageCode(blankToNull(columns[10]));
		return record;
	}

	private String parseRegistrationNumber(String rawValue, int rowNumber) {
		String registrationNumber = rawValue.trim();
		if (!registrationNumber.matches("\\d{8}")) {
			throw new IllegalArgumentException("Invalid registration number at row " + rowNumber);
		}
		return registrationNumber;
	}

	private Double parseScore(String rawValue, String column, int rowNumber) {
		String value = rawValue.trim();
		if (value.isBlank()) {
			return null;
		}
		double score;
		try {
			score = Double.parseDouble(value);
		} catch (NumberFormatException exception) {
			throw new IllegalArgumentException("Invalid score at row " + rowNumber + ", column " + column, exception);
		}
		if (score < 0 || score > 10) {
			throw new IllegalArgumentException("Score out of range at row " + rowNumber + ", column " + column);
		}
		return score;
	}

	private String blankToNull(String rawValue) {
		String value = rawValue.trim();
		return value.isBlank() ? null : value;
	}

	private void persistBatch(List<ScoreRecord> batch) {
		scoreRecordRepository.saveAll(batch);
		entityManager.flush();
		entityManager.clear();
	}

	private Map<Subject, LevelCounter> createCounters() {
		Map<Subject, LevelCounter> counters = new EnumMap<>(Subject.class);
		Arrays.stream(Subject.values()).forEach(subject -> counters.put(subject, new LevelCounter()));
		return counters;
	}

	private void countRecord(ScoreRecord record, Map<Subject, LevelCounter> counters) {
		counters.forEach((subject, counter) -> counter.count(subject.readScore(record)));
	}

	private void persistStats(Map<Subject, LevelCounter> counters) {
		scoreLevelStatsRepository.deleteAll();
		scoreLevelStatsRepository.saveAll(counters.entrySet().stream()
				.map(entry -> entry.getValue().toEntity(entry.getKey()))
				.toList());
	}

	private void warmStatsFromExistingDataIfNeeded() {
		if (scoreLevelStatsRepository.count() > 0) {
			return;
		}
		scoreLevelStatsRepository.saveAll(scoreStatisticsRepository.countAllSubjectsByLevels().stream()
				.map(stats -> {
					ScoreLevelStats entity = new ScoreLevelStats();
					entity.setSubjectCode(stats.code());
					entity.setExcellent(stats.excellent());
					entity.setGood(stats.good());
					entity.setAverage(stats.average());
					entity.setBelowAverage(stats.belowAverage());
					entity.setMissing(stats.missing());
					return entity;
				})
				.toList());
	}

	private static class LevelCounter {
		private long excellent;
		private long good;
		private long average;
		private long belowAverage;
		private long missing;

		private void count(Double score) {
			if (score == null) {
				missing++;
			} else if (score >= 8) {
				excellent++;
			} else if (score >= 6) {
				good++;
			} else if (score >= 4) {
				average++;
			} else {
				belowAverage++;
			}
		}

		private ScoreLevelStats toEntity(Subject subject) {
			ScoreLevelStats stats = new ScoreLevelStats();
			stats.setSubjectCode(subject.getCode());
			stats.setExcellent(excellent);
			stats.setGood(good);
			stats.setAverage(average);
			stats.setBelowAverage(belowAverage);
			stats.setMissing(missing);
			return stats;
		}
	}
}
