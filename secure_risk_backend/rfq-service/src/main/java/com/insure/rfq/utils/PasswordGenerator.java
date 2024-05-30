package com.insure.rfq.utils;

import java.util.Random;

import org.springframework.stereotype.Component;

@Component
public class PasswordGenerator {
	public String getRandomPassword() {
		String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
		StringBuilder randomPassword = new StringBuilder();
		Random random = new Random();
		for (int i = 0; i < 8; i++) {
			int index = random.nextInt(chars.length());
			randomPassword.append(chars.charAt(index));
		}
		return randomPassword.toString();
	}
}
