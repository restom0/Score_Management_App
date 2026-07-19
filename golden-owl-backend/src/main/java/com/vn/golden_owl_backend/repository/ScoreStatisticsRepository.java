package com.vn.golden_owl_backend.repository;

import com.vn.golden_owl_backend.dto.SubjectLevelStatsResponse;
import com.vn.golden_owl_backend.model.Subject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class ScoreStatisticsRepository {
	@PersistenceContext
	private EntityManager entityManager;

	public List<SubjectLevelStatsResponse> countAllSubjectsByLevels() {
		Query query = entityManager.createNativeQuery("""
				select
					sum(case when math >= 8 then 1 else 0 end),
					sum(case when math < 8 and math >= 6 then 1 else 0 end),
					sum(case when math < 6 and math >= 4 then 1 else 0 end),
					sum(case when math < 4 then 1 else 0 end),
					sum(case when math is null then 1 else 0 end),
					sum(case when literature >= 8 then 1 else 0 end),
					sum(case when literature < 8 and literature >= 6 then 1 else 0 end),
					sum(case when literature < 6 and literature >= 4 then 1 else 0 end),
					sum(case when literature < 4 then 1 else 0 end),
					sum(case when literature is null then 1 else 0 end),
					sum(case when foreign_language >= 8 then 1 else 0 end),
					sum(case when foreign_language < 8 and foreign_language >= 6 then 1 else 0 end),
					sum(case when foreign_language < 6 and foreign_language >= 4 then 1 else 0 end),
					sum(case when foreign_language < 4 then 1 else 0 end),
					sum(case when foreign_language is null then 1 else 0 end),
					sum(case when physics >= 8 then 1 else 0 end),
					sum(case when physics < 8 and physics >= 6 then 1 else 0 end),
					sum(case when physics < 6 and physics >= 4 then 1 else 0 end),
					sum(case when physics < 4 then 1 else 0 end),
					sum(case when physics is null then 1 else 0 end),
					sum(case when chemistry >= 8 then 1 else 0 end),
					sum(case when chemistry < 8 and chemistry >= 6 then 1 else 0 end),
					sum(case when chemistry < 6 and chemistry >= 4 then 1 else 0 end),
					sum(case when chemistry < 4 then 1 else 0 end),
					sum(case when chemistry is null then 1 else 0 end),
					sum(case when biology >= 8 then 1 else 0 end),
					sum(case when biology < 8 and biology >= 6 then 1 else 0 end),
					sum(case when biology < 6 and biology >= 4 then 1 else 0 end),
					sum(case when biology < 4 then 1 else 0 end),
					sum(case when biology is null then 1 else 0 end),
					sum(case when history >= 8 then 1 else 0 end),
					sum(case when history < 8 and history >= 6 then 1 else 0 end),
					sum(case when history < 6 and history >= 4 then 1 else 0 end),
					sum(case when history < 4 then 1 else 0 end),
					sum(case when history is null then 1 else 0 end),
					sum(case when geography >= 8 then 1 else 0 end),
					sum(case when geography < 8 and geography >= 6 then 1 else 0 end),
					sum(case when geography < 6 and geography >= 4 then 1 else 0 end),
					sum(case when geography < 4 then 1 else 0 end),
					sum(case when geography is null then 1 else 0 end),
					sum(case when civic_education >= 8 then 1 else 0 end),
					sum(case when civic_education < 8 and civic_education >= 6 then 1 else 0 end),
					sum(case when civic_education < 6 and civic_education >= 4 then 1 else 0 end),
					sum(case when civic_education < 4 then 1 else 0 end),
					sum(case when civic_education is null then 1 else 0 end)
				from score_records
				""");

		Object[] row = (Object[]) query.getSingleResult();
		return List.of(
				toStats(Subject.MATH, row, 0),
				toStats(Subject.LITERATURE, row, 5),
				toStats(Subject.FOREIGN_LANGUAGE, row, 10),
				toStats(Subject.PHYSICS, row, 15),
				toStats(Subject.CHEMISTRY, row, 20),
				toStats(Subject.BIOLOGY, row, 25),
				toStats(Subject.HISTORY, row, 30),
				toStats(Subject.GEOGRAPHY, row, 35),
				toStats(Subject.CIVIC_EDUCATION, row, 40)
		);
	}

	private SubjectLevelStatsResponse toStats(Subject subject, Object[] row, int offset) {
		return new SubjectLevelStatsResponse(
				subject.getCode(),
				subject.getEnglishName(),
				subject.getVietnameseName(),
				toLong(row[offset]),
				toLong(row[offset + 1]),
				toLong(row[offset + 2]),
				toLong(row[offset + 3]),
				toLong(row[offset + 4])
		);
	}

	private long toLong(Object value) {
		return value == null ? 0L : ((Number) value).longValue();
	}
}
