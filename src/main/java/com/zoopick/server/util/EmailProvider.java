package com.zoopick.server.util;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Async
@Component
@RequiredArgsConstructor
public class EmailProvider {
    private static final String SUBJECT = "[명지대 분실물 찾기 서비스] 인증메일 입니다.";

    @Value("classpath:/templates/email-certification.html")
    private Resource resource;
    private final JavaMailSender javaMailSender;

    public void senderCertificationMail(String email, String certificationNumber) throws MessagingException, IOException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(message, true, "UTF-8");

        String htmlContent = getCertificationMessage(certificationNumber);

        messageHelper.setTo(email);
        messageHelper.setSubject(SUBJECT);
        messageHelper.setText(htmlContent, true);

        javaMailSender.send(message);
    }

    private String getCertificationMessage(String certificationNumber) throws IOException {
        String template = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
        return template.replace("${certificationNumber}", certificationNumber);
    }
}