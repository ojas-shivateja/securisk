package com.insure.rfq.generator;


import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubmitClaimsEmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    public void sendEmailWithAttachment(String subject, String message, List<String> to, byte[] attachmentData, String attachmentName) throws MessagingException, MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

        helper.setSubject(subject);
        helper.setText(message);
        helper.setTo(to.toArray(new String[0]));

        InputStreamSource attachmentSource = new ByteArrayResource(attachmentData);
        helper.addAttachment(attachmentName, attachmentSource);

        javaMailSender.send(mimeMessage);
    }
}
