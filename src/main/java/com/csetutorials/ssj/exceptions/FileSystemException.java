package com.csetutorials.ssj.exceptions;

public class FileSystemException extends RuntimeException {

	public FileSystemException(String message, Throwable e) {
		super(message, e);
	}

	public FileSystemException(String message) {
		super(message);
	}

}
