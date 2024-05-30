package com.insure.rfq.login.globalexception;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, String>> thowMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
		Map<String, String> getException = new HashMap<>();
		ex.getBindingResult().getFieldErrors().forEach(e -> {
			String error = (e).getField();
			String msg = e.getDefaultMessage();
			getException.put(error, msg);
		}

		);
		return new ResponseEntity<>(getException, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(InvalidUser.class)
	public ResponseEntity<Map<String, Object>> invalidUser(InvalidUser invalid) {
		Map<String, Object> map = new HashMap<>();
		map.put("message", invalid.getMessage());
		map.put("timestamp", new Date());
		map.put("status", HttpStatus.NOT_FOUND);
		return new ResponseEntity<>(map, HttpStatus.NOT_FOUND);

	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<Map<String, Object>> invalidPattern(ConstraintViolationException exe) {
		Map<String, Object> map = new HashMap<>();
		map.put("message",
				"Password must be at least 8 characters long and contain at least one uppercase letter, one number, and one special character.");
		map.put("timestamp", new Date());
		return new ResponseEntity<>(map, HttpStatus.NOT_FOUND);

	}

	@ExceptionHandler(ExpiredJwtException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<String> handleBadRequestException(BadRequestException ex) {
		return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(PasswordMismatchException.class)
	public ResponseEntity<Map<String, Object>> passwordMisMatch(PasswordMismatchException exception) {
		Map<String, Object> map = new HashMap<>();
		map.put("message", exception.getMessage());
		map.put("timestamp", new Date());

		return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
	}

}
