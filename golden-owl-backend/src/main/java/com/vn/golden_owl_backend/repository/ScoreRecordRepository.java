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

	@Query("""
			select new com.vn.golden_owl_backend.dto.TopGroupAStudentResponse(
				record.registrationNumber,
				record.math,
				record.physics,
				record.chemistry,
				(record.math + record.physics + record.chemistry)
			)
			from ScoreRecord record
			where record.math is not null
				and record.physics is not null
				and record.chemistry is not null
			order by (record.math + record.physics + record.chemistry) desc, record.registrationNumber asc
			""")
	List<TopGroupAStudentResponse> findTopGroupA(Pageable pageable);
}
