package com.insure.rfq.login.globalexception;

public class InvalidUser extends RuntimeException {

	public InvalidUser(String message) {
		super(message);
	}

}
