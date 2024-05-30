package com.insure.rfq.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class EmailSender {

	@Autowired
	private JavaMailSender javaMailSender;

	@Autowired
	private OtpGenerator otpGen;
	

	public String sendEmailforOtp(String to) throws EmailException {
		try {
			SimpleMailMessage message = new SimpleMailMessage();
			message.setTo(to);
			message.setSubject("otp for forgot password");
			String otp = otpGen.getOtp();
			message.setText("otp : " + otp);
			javaMailSender.send(message);
			return otp;
		} catch (MailException e) {
			throw new EmailException("Error sending email", e);
		}
	}

	public String sendRandomPassword(String email, String passCode) {

		try {
			SimpleMailMessage message = new SimpleMailMessage();
			message.setTo(email);
			message.setSubject("securisk password");
			message.setText("password : " + passCode);

			javaMailSender.send(message);
			return passCode;
		} catch (MailException e) {
			throw new EmailException("Error sending email", e);
		}

	}
}
