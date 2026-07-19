package com.vn.golden_owl_backend.exception;

public class ScoreNotFoundException extends RuntimeException {
	public ScoreNotFoundException(String registrationNumber) {
		super("No score found for registration number: " + registrationNumber);
	}
}
