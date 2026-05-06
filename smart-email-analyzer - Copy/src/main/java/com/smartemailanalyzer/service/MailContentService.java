package com.smartemailanalyzer.service;

import jakarta.mail.BodyPart;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.Part;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Extracts and cleans message content from Jakarta Mail messages.
 */
@Service
public class MailContentService {

    public String extractText(Message message) throws MessagingException, IOException {
        String text = extractText((Part) message);
        return cleanText(text);
    }

    private String extractText(Part part) throws MessagingException, IOException {
        if (part.isMimeType("text/plain")) {
            Object content = part.getContent();
            return content == null ? "" : content.toString();
        }

        if (part.isMimeType("text/html")) {
            Object content = part.getContent();
            return content == null ? "" : Jsoup.parse(content.toString()).text();
        }

        if (part.isMimeType("multipart/*")) {
            Multipart multipart = (Multipart) part.getContent();
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < multipart.getCount(); i++) {
                BodyPart bodyPart = multipart.getBodyPart(i);
                builder.append(extractText(bodyPart)).append("\n");
            }
            return builder.toString();
        }

        if (part.isMimeType("message/rfc822")) {
            Object content = part.getContent();
            if (content instanceof Message nestedMessage) {
                return extractText(nestedMessage);
            }
        }

        return "";
    }

    public String cleanText(String rawContent) {
        if (rawContent == null || rawContent.isBlank()) {
            return "No readable content available.";
        }

        String normalized = rawContent.replace("\r", "\n");
        String withoutHtml = normalized.contains("<") ? Jsoup.parse(normalized).text() : normalized;

        return Arrays.stream(withoutHtml.split("\n"))
                .map(String::trim)
                .filter(line -> !line.isBlank())
                .collect(Collectors.joining("\n"));
    }
}
