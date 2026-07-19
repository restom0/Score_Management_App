package com.vn.golden_owl_backend.model;

import java.util.Arrays;
import java.util.function.Function;

public enum Subject {
	MATH("math", "toan", "Math", "Toan", "Toán", ScoreRecord::getMath),
	LITERATURE("literature", "ngu_van", "Literature", "Ngu van", "Ngữ văn", ScoreRecord::getLiterature),
	FOREIGN_LANGUAGE("foreignLanguage", "ngoai_ngu", "Foreign language", "Ngoai ngu", "Ngoại ngữ", ScoreRecord::getForeignLanguage),
	PHYSICS("physics", "vat_li", "Physics", "Vat li", "Vật lí", ScoreRecord::getPhysics),
	CHEMISTRY("chemistry", "hoa_hoc", "Chemistry", "Hoa hoc", "Hóa học", ScoreRecord::getChemistry),
	BIOLOGY("biology", "sinh_hoc", "Biology", "Sinh hoc", "Sinh học", ScoreRecord::getBiology),
	HISTORY("history", "lich_su", "History", "Lich su", "Lịch sử", ScoreRecord::getHistory),
	GEOGRAPHY("geography", "dia_li", "Geography", "Dia li", "Địa lí", ScoreRecord::getGeography),
	CIVIC_EDUCATION("civicEducation", "gdcd", "Civic education", "GDCD", "GDCD", ScoreRecord::getCivicEducation);

	private final String code;
	private final String csvColumn;
	private final String englishName;
	private final String vietnameseNameAscii;
	private final String vietnameseName;
	private final Function<ScoreRecord, Double> scoreReader;

	Subject(
			String code,
			String csvColumn,
			String englishName,
			String vietnameseNameAscii,
			String vietnameseName,
			Function<ScoreRecord, Double> scoreReader
	) {
		this.code = code;
		this.csvColumn = csvColumn;
		this.englishName = englishName;
		this.vietnameseNameAscii = vietnameseNameAscii;
		this.vietnameseName = vietnameseName;
		this.scoreReader = scoreReader;
	}

	public String getCode() {
		return code;
	}

	public String getCsvColumn() {
		return csvColumn;
	}

	public String getEnglishName() {
		return englishName;
	}

	public String getVietnameseNameAscii() {
		return vietnameseNameAscii;
	}

	public String getVietnameseName() {
		return vietnameseName;
	}

	public Double readScore(ScoreRecord scoreRecord) {
		return scoreReader.apply(scoreRecord);
	}

	public static Subject fromCode(String code) {
		return Arrays.stream(values())
				.filter(subject -> subject.code.equals(code))
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException("Unsupported subject: " + code));
	}
}
