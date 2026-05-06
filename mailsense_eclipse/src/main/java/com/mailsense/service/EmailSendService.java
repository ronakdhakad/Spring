package com.mailsense.service;

import com.mailsense.dao.impl.SentEmailDaoImpl;
import com.mailsense.dao.impl.UserEmailConfigDaoImpl;
import com.mailsense.dto.SendEmailDto;
import com.mailsense.entity.SentEmail;
import com.mailsense.entity.User;
import com.mailsense.entity.UserEmailConfig;
import com.mailsense.util.AesEncryptionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

@Service
@Slf4j
public class EmailSendService {

    @Autowired private UserEmailConfigDaoImpl configDao;
    @Autowired private SentEmailDaoImpl       sentEmailDao;
    @Autowired private AesEncryptionUtil      aesUtil;
    @Autowired private AuditService           auditService;

    @Transactional
    public void send(SendEmailDto dto, User user) {
        UserEmailConfig config = configDao.findByUserId(user.getId())
            .orElseThrow(() -> new RuntimeException("No email config for user"));

        String plainPass = aesUtil.decrypt(
            config.getAppPasswordEncrypted(), config.getEncryptionIv());

        try {
            Properties props = new Properties();
            props.put("mail.smtp.host",              config.getSmtpHost());
            props.put("mail.smtp.port",              String.valueOf(config.getSmtpPort()));
            props.put("mail.smtp.auth",              "true");
            props.put("mail.smtp.starttls.enable",   "true");
            props.put("mail.smtp.connectiontimeout", "10000");
            props.put("mail.smtp.timeout",           "10000");

            final String pass = plainPass;
            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(config.getEmailAddress(), pass);
                }
            });

            MimeMessage msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(config.getEmailAddress()));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(dto.getToAddress()));

            if (dto.getCcAddress() != null && !dto.getCcAddress().isBlank())
                msg.setRecipients(Message.RecipientType.CC, InternetAddress.parse(dto.getCcAddress()));

            if (dto.getInReplyToMessageId() != null) {
                msg.setHeader("In-Reply-To", dto.getInReplyToMessageId());
                msg.setHeader("References",  dto.getInReplyToMessageId());
            }

            msg.setSubject(dto.getSubject(), "UTF-8");

            String body = dto.getBody();
            if (SentEmail.SendType.FORWARD.equals(dto.getSendType()) && dto.getOriginalBody() != null)
                body += "\n\n--- Forwarded Message ---\n" + dto.getOriginalBody();

            msg.setContent(body.replace("\n", "<br>"), "text/html; charset=UTF-8");
            Transport.send(msg);
            log.info("Email sent from {} to {}", config.getEmailAddress(), dto.getToAddress());

        } catch (MessagingException e) {
            log.error("SMTP failed for user {}: {}", user.getId(), e.getMessage());
            throw new RuntimeException("Send failed: " + e.getMessage(), e);
        }

        sentEmailDao.save(SentEmail.builder()
            .user(user)
            .inReplyToEmailId(dto.getInReplyToEmailId())
            .toAddress(dto.getToAddress())
            .ccAddress(dto.getCcAddress())
            .subject(dto.getSubject())
            .body(dto.getBody())
            .sendType(dto.getSendType())
            .build());

        auditService.logById(user.getId(), "EMAIL_SENT",
            dto.getSendType() + " to: " + dto.getToAddress());
    }
}
