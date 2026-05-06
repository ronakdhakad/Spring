package com.mailsense.service;

import com.mailsense.dao.EmailDao;
import com.mailsense.dao.impl.UserEmailConfigDaoImpl;
import com.mailsense.entity.Email;
import com.mailsense.entity.User;
import com.mailsense.entity.UserEmailConfig;
import com.mailsense.util.AesEncryptionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.List;
import java.util.Properties;

@Service
@Slf4j
public class DigestService {

    @Autowired private EmailDao               emailDao;
    @Autowired private UserEmailConfigDaoImpl configDao;
    @Autowired private AesEncryptionUtil      aesUtil;
    @Autowired private AuditService           auditService;

    @Transactional
    public void sendDigest(User user) {
        Long userId = user.getId();

        long critical = emailDao.countByUserIdAndUrgencyLevel(userId, Email.UrgencyLevel.CRITICAL);
        long high     = emailDao.countByUserIdAndUrgencyLevel(userId, Email.UrgencyLevel.HIGH);
        long medium   = emailDao.countByUserIdAndUrgencyLevel(userId, Email.UrgencyLevel.MEDIUM);
        long low      = emailDao.countByUserIdAndUrgencyLevel(userId, Email.UrgencyLevel.LOW);
        long unread   = emailDao.countUnreadByUserId(userId);
        long total    = critical + high + medium + low;
        List<Object[]> topSenders = emailDao.findTopSendersByUserId(userId, 5);

        UserEmailConfig config = configDao.findByUserId(userId).orElse(null);
        if (config == null) { log.warn("No config for user {} - skipping digest", userId); return; }

        String plainPass = aesUtil.decrypt(config.getAppPasswordEncrypted(), config.getEncryptionIv());

        try {
            Properties props = new Properties();
            props.put("mail.smtp.host",            config.getSmtpHost());
            props.put("mail.smtp.port",            String.valueOf(config.getSmtpPort()));
            props.put("mail.smtp.auth",            "true");
            props.put("mail.smtp.starttls.enable", "true");

            final String pass = plainPass;
            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(config.getEmailAddress(), pass);
                }
            });

            MimeMessage msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(config.getEmailAddress()));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(user.getEmail()));
            msg.setSubject("Your MailSense Weekly Digest");
            msg.setContent(buildHtml(user.getFullName(), total, critical, high, unread, topSenders),
                           "text/html; charset=UTF-8");
            Transport.send(msg);

            log.info("Digest sent to {}", user.getEmail());
            auditService.logById(userId, "DIGEST_SENT", "Sent to " + user.getEmail());

        } catch (MessagingException e) {
            log.error("Digest failed for user {}: {}", userId, e.getMessage());
            throw new RuntimeException("Digest send failed", e);
        }
    }

    private String buildHtml(String name, long total, long critical,
                              long high, long unread, List<Object[]> senders) {
        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html><html><head><meta charset='UTF-8'>")
          .append("<style>body{font-family:Arial,sans-serif;max-width:600px;margin:0 auto;padding:20px}")
          .append("h1{color:#2563eb}.stat{display:inline-block;background:#f1f5f9;border-radius:8px;")
          .append("padding:16px;margin:8px;text-align:center;min-width:100px}")
          .append(".num{font-size:28px;font-weight:bold}.c{color:#dc2626}.h{color:#ea580c}")
          .append(".u{color:#2563eb}.t{color:#16a34a}table{width:100%;border-collapse:collapse}")
          .append("th,td{text-align:left;padding:8px;border-bottom:1px solid #e2e8f0}</style></head><body>")
          .append("<h1>MailSense Weekly Digest</h1>")
          .append("<p>Hi ").append(name).append(", here's your weekly inbox summary.</p>")
          .append("<div>")
          .append("<div class='stat t'><div class='num'>").append(total).append("</div><div>Total</div></div>")
          .append("<div class='stat c'><div class='num'>").append(critical).append("</div><div>Critical</div></div>")
          .append("<div class='stat h'><div class='num'>").append(high).append("</div><div>High</div></div>")
          .append("<div class='stat u'><div class='num'>").append(unread).append("</div><div>Unread</div></div>")
          .append("</div>");
        if (!senders.isEmpty()) {
            sb.append("<h2>Top Senders</h2><table><thead><tr><th>#</th><th>Sender</th><th>Count</th></tr></thead><tbody>");
            int r = 1;
            for (Object[] row : senders)
                sb.append("<tr><td>").append(r++).append("</td><td>").append(row[0])
                  .append("</td><td>").append(row[1]).append("</td></tr>");
            sb.append("</tbody></table>");
        }
        sb.append("<p style='color:#94a3b8;font-size:12px;margin-top:32px'>Sent by MailSense</p></body></html>");
        return sb.toString();
    }
}
