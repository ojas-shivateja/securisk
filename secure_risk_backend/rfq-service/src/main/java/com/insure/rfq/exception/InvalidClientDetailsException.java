package com.insure.rfq.exception;

public class InvalidClientDetailsException extends RuntimeException {

    public InvalidClientDetailsException (String message){
        super(message);
    }
}
