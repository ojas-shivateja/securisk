package com.insure.rfq.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.insure.rfq.dto.EmailFileDTo;
import com.insure.rfq.dto.EmailRequest;
import com.insure.rfq.entity.CorporateDetailsEntity;
import com.insure.rfq.entity.InsureUsers;
import com.insure.rfq.repository.CorporateDetailsRepository;
import com.insure.rfq.repository.InsurerUsersRepository;

import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
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
import jakarta.mail.util.ByteArrayDataSource;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EmailAlongAttachement {
	@Autowired
	private InsurerUsersRepository repository;
	@Autowired
	private CorporateDetailsRepository corporateDetailsRepository;
	@Value("${spring.mail.host}")
	private String host;
	@Value("${spring.mail.username}")
	private String username;
	@Value("${spring.mail.password}")
	private String password;
	@Value("${spring.mail.port}")
	private int port;
	@Value("${spring.mail.properties.mail.smtp.auth}")
	private String smtpAuth;
	@Value("${spring.mail.properties.mail.smtp.starttls.required}")
	private String starttls;

	public String sendEmailWithAttachment(EmailRequest emailRequest) {
		String senderEmail = username;
		int count=0;

		Properties properties = new Properties();
		properties.put("mail.smtp.auth", smtpAuth);
		properties.put("mail.smtp.starttls.enable", "true");
		properties.put("mail.smtp.host", host);
		properties.put("mail.smtp.port", port);

		Session session = Session.getInstance(properties, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});

		try {
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(senderEmail));
			message.setSubject("RFQ ATTACHMENTS ");

			List<String> recipients = emailRequest.getTo();
			List<EmailFileDTo> attachments = emailRequest.getGetAllFiles();

			Optional<CorporateDetailsEntity> findByRfqId = corporateDetailsRepository
					.findByRfqId(emailRequest.getRfqId());
			if (recipients != null && !recipients.isEmpty()) {
				for (String recipient : recipients) {
					Multipart multipart = new MimeMultipart();
					InsureUsers user = repository.findUserByEmail(recipient);
					log.info("Recipient From sendEmailWithAttachment", recipient);

					// Set the email body content
					String emailContent = "Dear " + user.getManagerName() + "\n Please find attached the GHI RFQ for "
							+ findByRfqId.get().getInsuredName() + ". This is a " + findByRfqId.get().getPolicyType()
							+ " Policy.\n" + "Kindly provide your competitive quote as per RFQ at the earliest.\n\n"
							+ "" + "For any queries, please do not hesitate to contact us.\n\n" + "Regards,\n\n"
							+ "Securisk private Limited " + "\n" + "Phone number: " + findByRfqId.get().getPhNo() + "\n" + "Email: "
							+ findByRfqId.get().getEmail();

					MimeBodyPart textPart = new MimeBodyPart();
					textPart.setText(emailContent);
					multipart.addBodyPart(textPart);

					// Add attachments
					if (attachments != null && attachments.size() > 0) {
						for (EmailFileDTo fileData : attachments) {
							try {
								MimeBodyPart attachmentPart = new MimeBodyPart();

								if (!fileData.getFilename().equalsIgnoreCase("employee_Details")
										|| !fileData.getFilename().equalsIgnoreCase("Mandate_Letter")) {
									log.info("byte [] convert into zip ");
									DataSource source = new ByteArrayDataSource(
											createZipFromByteArray(fileData.getFileByteData(), fileData.getFilename()),
											"application/octet-stream"); // Create a DataSource from the byte array
									attachmentPart.setDataHandler(new DataHandler(source));
									attachmentPart.setFileName(fileData.getFilename() + ".zip"); // Set the desired
																									// attachment file
																									// name
									multipart.addBodyPart(attachmentPart);
									count++;

								}

							} catch (MessagingException e) {
								e.printStackTrace(); // Handle the exception appropriately
							}
						}

					}
					if (emailRequest.getFilePath().size() > 0) {
						for (String filePath : emailRequest.getFilePath()) {

							

							log.info("file convert into zip ");

							for (String filePath1 : emailRequest.getFilePath()) {
								MimeBodyPart sendFile = new MimeBodyPart();
								String[] fileNameSplit = filePath1.split("\\\\");
								// spit to find file name
								String filePathName = fileNameSplit[fileNameSplit.length - 1];
								log.info("file name {}", filePathName);
								// to to find name from 2nd last element with . extension
								String[] splitTheFile = filePathName.split("\\.");
								// split to segrigate file name with with . extension
								log.info(" file path to convert into byte[] and attached in email: {} ",
										splitTheFile[0]);
								if (splitTheFile[splitTheFile.length - 1].equalsIgnoreCase("xls") || splitTheFile[splitTheFile.length - 1].equalsIgnoreCase("xlsx")) {
									DataSource source = new ByteArrayDataSource(zipFile(filePath1.trim()),
											"application/octet-stream");
									sendFile.setDataHandler(new DataHandler(source));
									sendFile.setFileName("employee_Details.zip");

									multipart.addBodyPart(sendFile);
									log.info("if for xlx true");
									count++;

								} 
								 if (splitTheFile[splitTheFile.length - 1].equalsIgnoreCase("pdf")) {

									DataSource source = new ByteArrayDataSource(zipFile(filePath1.trim()),
											"application/octet-stream");
									sendFile.setDataHandler(new DataHandler(source));
									sendFile.setFileName("MandateLetter.zip");
									multipart.addBodyPart(sendFile);
									log.info("if for pdf true");
									count++;
								}

							}

						}
					}

					// Set the multipart content to the message
					message.setContent(multipart);

					// Set the recipient and send the email
					message.setRecipients(Message.RecipientType.TO, user.getEmail());
					Transport.send(message);
					
					log.info(user.getEmail() + " email sent with " + count + " attachement");
				}
			}

		} catch (

		MessagingException e) {
			e.printStackTrace();
		}
		return " email sent with " + emailRequest.getGetAllFiles().size() + " attachement";
	}

	public static byte[] createZipFromByteArray(byte[] inputData, String fileName) {
		try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
				ZipOutputStream zos = new ZipOutputStream(byteArrayOutputStream)) {

			ZipEntry zipEntry = new ZipEntry(fileName + ".pdf");
			zos.putNextEntry(zipEntry);

			// Write the input data to the zip entry
			zos.write(inputData, 0, inputData.length);
			zos.closeEntry();

			zos.finish();

			return byteArrayOutputStream.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null; // Return null in case of an error
	}

	public byte[] zipFile(String filePath) {
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ZipOutputStream zipOut = new ZipOutputStream(baos);
				FileInputStream fis = new FileInputStream(filePath)) {

			File fileToZip = new File(filePath);
			ZipEntry zipEntry = new ZipEntry(fileToZip.getName()); // Use the file name for the entry

			zipOut.putNextEntry(zipEntry);

			byte[] buffer = new byte[1024];
			int bytesRead;
			while ((bytesRead = fis.read(buffer)) != -1) {
				zipOut.write(buffer, 0, bytesRead);
			}

			zipOut.closeEntry();
			zipOut.finish();
			return baos.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

}
