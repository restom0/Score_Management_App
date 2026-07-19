package com.vn.golden_owl_backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

@Entity
@Table(
		name = "score_records",
		indexes = {
				@Index(name = "idx_score_records_registration_number", columnList = "registration_number", unique = true)
		}
)
public class ScoreRecord {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "registration_number", nullable = false, unique = true, length = 8)
	private String registrationNumber;

	@Column(name = "math")
	private Double math;

	@Column(name = "literature")
	private Double literature;

	@Column(name = "foreign_language")
	private Double foreignLanguage;

	@Column(name = "physics")
	private Double physics;

	@Column(name = "chemistry")
	private Double chemistry;

	@Column(name = "biology")
	private Double biology;

	@Column(name = "history")
	private Double history;

	@Column(name = "geography")
	private Double geography;

	@Column(name = "civic_education")
	private Double civicEducation;

	@Column(name = "foreign_language_code", length = 8)
	private String foreignLanguageCode;

	public Long getId() {
		return id;
	}

	public String getRegistrationNumber() {
		return registrationNumber;
	}

	public void setRegistrationNumber(String registrationNumber) {
		this.registrationNumber = registrationNumber;
	}

	public Double getMath() {
		return math;
	}

	public void setMath(Double math) {
		this.math = math;
	}

	public Double getLiterature() {
		return literature;
	}

	public void setLiterature(Double literature) {
		this.literature = literature;
	}

	public Double getForeignLanguage() {
		return foreignLanguage;
	}

	public void setForeignLanguage(Double foreignLanguage) {
		this.foreignLanguage = foreignLanguage;
	}

	public Double getPhysics() {
		return physics;
	}

	public void setPhysics(Double physics) {
		this.physics = physics;
	}

	public Double getChemistry() {
		return chemistry;
	}

	public void setChemistry(Double chemistry) {
		this.chemistry = chemistry;
	}

	public Double getBiology() {
		return biology;
	}

	public void setBiology(Double biology) {
		this.biology = biology;
	}

	public Double getHistory() {
		return history;
	}

	public void setHistory(Double history) {
		this.history = history;
	}

	public Double getGeography() {
		return geography;
	}

	public void setGeography(Double geography) {
		this.geography = geography;
	}

	public Double getCivicEducation() {
		return civicEducation;
	}

	public void setCivicEducation(Double civicEducation) {
		this.civicEducation = civicEducation;
	}

	public String getForeignLanguageCode() {
		return foreignLanguageCode;
	}

	public void setForeignLanguageCode(String foreignLanguageCode) {
		this.foreignLanguageCode = foreignLanguageCode;
	}
}
