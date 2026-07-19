package com.vn.golden_owl_backend.repository;

import com.vn.golden_owl_backend.dto.TopGroupAStudentResponse;
import com.vn.golden_owl_backend.model.ScoreRecord;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ScoreRecordRepository extends JpaRepository<ScoreRecord, Long> {
	Optional<ScoreRecord> findByRegistrationNumber(String registrationNumber);

	@Query(
			"select new com.vn.golden_owl_backend.dto.TopGroupAStudentResponse("
					+ "score.registrationNumber, "
					+ "score.math, "
					+ "score.physics, "
					+ "score.chemistry, "
					+ "(score.math + score.physics + score.chemistry)) "
					+ "from ScoreRecord score "
					+ "where score.math is not null "
					+ "and score.physics is not null "
					+ "and score.chemistry is not null "
					+ "order by (score.math + score.physics + score.chemistry) desc, score.registrationNumber asc"
	)
	List<TopGroupAStudentResponse> findTopGroupA(Pageable pageable);
}
