package com.insure.rfq.utils;

import java.util.function.Supplier;

import org.springframework.stereotype.Component;

@Component
public class OtpGenerator {
	public String getOtp() {

		Supplier<String> otp = () -> {
			String generateOtp = "";
			for (int i = 1; i <= 6; i++) {
				generateOtp = generateOtp + (int) (Math.random() * 10);
			}
			return generateOtp;
		};
		return otp.get();
	}
}
