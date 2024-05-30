package com.insure.rfq.login.securityconfiguration;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {
		if (authException instanceof BadCredentialsException) {
			// Handle invalid paths (Bad Request)
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Bad Request: " + authException.getMessage());
		} else {
			// Handle other authentication failures (Unauthorized)
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
					"Authentication Failed: " + authException.getMessage());
		}
	}

	private static class ErrorResponse {
		private final String error;
		private final String message;

		public ErrorResponse(String error, String message) {
			this.error = error;
			this.message = message;
		}

		public String getError() {
			return error;
		}

		public String getMessage() {
			return message;
		}
	}
}
