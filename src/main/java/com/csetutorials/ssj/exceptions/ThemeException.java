package com.csetutorials.ssj.exceptions;

public class ThemeException extends RuntimeException {

	public ThemeException(String message, Throwable e) {
		super(message, e);
	}

	public ThemeException(String message) {
		super(message);
	}
}
