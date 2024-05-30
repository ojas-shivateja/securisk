package com.insure.rfq.login.globalexception;

public class BadRequestException  extends RuntimeException{
	 public BadRequestException(String message) {
	        super(message);
	    }
}
