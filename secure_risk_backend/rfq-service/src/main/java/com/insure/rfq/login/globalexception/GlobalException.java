package com.insure.rfq.login.globalexception;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.EOFException;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalException {
	@ExceptionHandler(Exception.class)
    public ProblemDetail getProblemDetailsObject(Exception ex)
    {
		ProblemDetail error=null;
    	if(ex instanceof BadCredentialsException)
    	{
    		 error=ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(401),ex.getMessage());
    		error.setProperty("access_denied_reason","Authentication Failure");
    	}
    	if(ex instanceof AccessDeniedException)
    	{
    		error=ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(403),ex.getMessage());
    		error.setProperty("access_denied_reason","Not_Authorize");
    	}
    	if(ex instanceof SignatureException)
    	{
    		error=ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(403),ex.getMessage());
    		error.setProperty("access_denied","JWt signature invalid");
    	}
    	if(ex instanceof ExpiredJwtException)
    	{
    		error=ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(401),ex.getMessage());
    		error.setProperty("access_denied","JWT Token Expired");
    	}
    	if(ex instanceof EOFException)
    	{
    		error=ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(401),ex.getMessage());
    		error.setProperty("access_denied","not header present");
    	}
    	if(ex instanceof NoSuchElementException)
    	{
    		error=ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(404), ex.getMessage());
    		error.setProperty("no such element in data base "," no data avialable " );
    	}
    	if(ex instanceof NullPointerException)
    	{
    		error=ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(404), ex.getMessage());
    		error.setProperty(" data is null ","null data " );
    	}
    	
		return error;
    }
	@ExceptionHandler(ExpiredJwtException.class)
	@ResponseStatus(value=HttpStatus.UNAUTHORIZED)
	public Map<String,String>handleExpiredJwtException(ExpiredJwtException exception)
	{
		Map<String,String>data= new HashMap<>();
		data.put("access denied", exception.getMessage());
		return data;
	}
	
}
