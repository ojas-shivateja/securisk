package com.insure.rfq.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatusCode;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExceptionEntity {
	private LocalDateTime timeStamp;
	private String message;
	private HttpStatusCode statusResponse;
	private int  statusCode;

}
