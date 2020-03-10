package com.lmsnet.box.exception;

public class BoxException extends Exception {

	/**
     * 
     */
    private static final long serialVersionUID = 1L;

	public BoxException(final String message) {
		super(message);
	}

	public BoxException(final String message, final Throwable cause) {
		super(message, cause);
	}
}