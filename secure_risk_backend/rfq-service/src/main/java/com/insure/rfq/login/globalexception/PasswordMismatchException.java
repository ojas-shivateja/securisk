package com.insure.rfq.login.globalexception;

public class PasswordMismatchException extends RuntimeException {

	public PasswordMismatchException(String message) {
		super(message);
	}

}
