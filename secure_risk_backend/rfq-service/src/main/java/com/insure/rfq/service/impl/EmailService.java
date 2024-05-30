package com.insure.rfq.service.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.insure.rfq.dto.EmailRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.activation.FileDataSource;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;

@Service
public class EmailService {
	@Value("${spring.mail.host}")
	private String host ;
	@Value("${spring.mail.username}")
	private String username;
	@Value("${spring.mail.password}")
	private String password ;
	@Value("${spring.mail.port}")
	private int port;
	@Value("${spring.mail.properties.mail.smtp.auth}")
	private String smtpAuth;	
	@Value("${spring.mail.properties.mail.smtp.starttls.enable}")
	private String starttls;



	public void sendEmailWithAttachment(EmailRequest emailRequest) {
		// Email configuration
	

		// Sender and recipient email addresses
		String senderEmail = username;

		// Set the mail properties
	
		Properties properties = new Properties();
		properties.put("mail.smtp.auth", smtpAuth);
		properties.put("mail.smtp.starttls.enable", starttls);
		properties.put("mail.smtp.host", host);
		properties.put("mail.smtp.port", port);

		// Create a Session object with the authentication credentials
		Session session = Session.getInstance(properties, new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});

		try {
			// Create a MimeMessage object
			MimeMessage message = new MimeMessage(session);
			// Set the sender and recipient addresses
			message.setFrom(new InternetAddress(senderEmail));
			// Set the email subject and body
			message.setSubject("Test Email from Securisk");
			message.setText("This is a mail regarding testing purpose of send email functionality");
			List<String> recipients = emailRequest.getTo();
			System.out.println("recipients :: " + recipients);
			if (recipients != null && !recipients.isEmpty()) {
				List<InternetAddress> validRecipients = new ArrayList<>();
				for (String recipient : recipients) {
					if (recipient != null && !recipient.trim().isEmpty()) {
						validRecipients.add(new InternetAddress(recipient.trim()));
					}
				}
				if (!validRecipients.isEmpty()) {
					message.addRecipients(Message.RecipientType.TO, validRecipients.toArray(new InternetAddress[0]));
				} else {
					System.out.println("No valid recipients found.");
				}
			} else {
				System.out.println("No recipients provided.");
			}
			
			Multipart multipart = new MimeMultipart();
			//Below condition is to not make attachments mandatory
			if(emailRequest.getFilePath() != null) {
				// Attach multiple files to the email
				List<String> filePaths = emailRequest.getFilePath();
				System.out.println("filePaths :: " + filePaths);
				for (String filePath : filePaths) {
					File file = new File(filePath);
					if (file.exists()) {
						MimeBodyPart messageBodyPart2 = new MimeBodyPart();
						DataSource source = new FileDataSource(file);
						messageBodyPart2.setDataHandler(new DataHandler(source));
						messageBodyPart2.setFileName(file.getName());
						multipart.addBodyPart(messageBodyPart2);
					} else {
						System.out.println("File not found: " + filePath);
					}
				}
				message.setContent(multipart);
				Transport.send(message);
			}else {
				Transport.send(message);
			}
		} catch (MessagingException e) {
			e.printStackTrace();
			e.getMessage();
		}

	}
}
