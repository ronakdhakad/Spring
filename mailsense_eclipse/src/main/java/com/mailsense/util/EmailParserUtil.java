package com.mailsense.util;

import org.apache.commons.lang3.StringUtils;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Cleans raw HTML email content into readable plain text.
 */
public final class EmailParserUtil {

    private EmailParserUtil() {}

    private static final Pattern HTML_TAG = Pattern.compile("<[^>]+>", Pattern.DOTALL);
    private static final int PREVIEW_LENGTH = 500;
    private static final List<String> SIG_MARKERS = Arrays.asList(
        "--", "-- ", "Regards,", "Best regards,", "Thanks,",
        "Thank you,", "Sincerely,", "Cheers,", "Best,",
        "Sent from my iPhone", "Sent from my Android"
    );

    public static String htmlToPlainText(String html) {
        if (StringUtils.isBlank(html)) return "";
        String text = html;
        text = text.replaceAll("(?i)<br\\s*/?>", "\n");
        text = text.replaceAll("(?i)</(p|div|tr|li)>", "\n");
        text = HTML_TAG.matcher(text).replaceAll("");
        text = decodeEntities(text);
        text = text.replaceAll("(\r?\n){3,}", "\n\n");
        return text.strip();
    }

    public static String removeSignature(String body) {
        if (StringUtils.isBlank(body)) return "";
        String[] lines = body.split("\n");
        StringBuilder sb = new StringBuilder();
        for (String line : lines) {
            String t = line.strip();
            boolean isSig = SIG_MARKERS.stream().anyMatch(m ->
                t.equalsIgnoreCase(m) || t.startsWith(m));
            if (isSig) break;
            sb.append(line).append("\n");
        }
        return sb.toString().strip();
    }

    public static String createPreview(String body) {
        if (StringUtils.isBlank(body)) return "";
        String oneLine = body.replaceAll("\\s+", " ").strip();
        return oneLine.length() <= PREVIEW_LENGTH
            ? oneLine : oneLine.substring(0, PREVIEW_LENGTH) + "…";
    }

    public static ParsedEmail parse(String rawHtml) {
        String plain   = htmlToPlainText(rawHtml);
        String cleaned = removeSignature(plain);
        return new ParsedEmail(cleaned, createPreview(cleaned));
    }

    private static String decodeEntities(String text) {
        return text.replace("&amp;", "&").replace("&lt;", "<")
                   .replace("&gt;", ">").replace("&quot;", "\"")
                   .replace("&#39;", "'").replace("&nbsp;", " ");
    }

    public static class ParsedEmail {
        public final String fullBody;
        public final String preview;
        ParsedEmail(String fullBody, String preview) {
            this.fullBody = fullBody;
            this.preview  = preview;
        }
    }
}
