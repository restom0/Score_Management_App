package com.vn.golden_owl_backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "score_level_stats")
public class ScoreLevelStats {
	@Id
	@Column(name = "subject_code", nullable = false, length = 32)
	private String subjectCode;

	@Column(name = "excellent", nullable = false)
	private long excellent;

	@Column(name = "good", nullable = false)
	private long good;

	@Column(name = "average", nullable = false)
	private long average;

	@Column(name = "below_average", nullable = false)
	private long belowAverage;

	@Column(name = "missing", nullable = false)
	private long missing;

	public String getSubjectCode() {
		return subjectCode;
	}

	public void setSubjectCode(String subjectCode) {
		this.subjectCode = subjectCode;
	}

	public long getExcellent() {
		return excellent;
	}

	public void setExcellent(long excellent) {
		this.excellent = excellent;
	}

	public long getGood() {
		return good;
	}

	public void setGood(long good) {
		this.good = good;
	}

	public long getAverage() {
		return average;
	}

	public void setAverage(long average) {
		this.average = average;
	}

	public long getBelowAverage() {
		return belowAverage;
	}

	public void setBelowAverage(long belowAverage) {
		this.belowAverage = belowAverage;
	}

	public long getMissing() {
		return missing;
	}

	public void setMissing(long missing) {
		this.missing = missing;
	}
}
