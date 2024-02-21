package com.csetutorials.ssj.exceptions;

public class JsonParsingException extends RuntimeException {

	public JsonParsingException(Throwable e) {
		super(e);
	}

	public JsonParsingException(String message, Throwable e) {
		super(message, e);
	}
}
