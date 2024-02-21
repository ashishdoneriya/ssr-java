package com.csetutorials.ssj.exceptions;

public class MetaDataException extends RuntimeException {

	public MetaDataException(Throwable e) {
		super(e);
	}

	public MetaDataException(String message) {
		super(message);
	}

	public MetaDataException(String message, Throwable e) {
		super(message, e);
	}

}
