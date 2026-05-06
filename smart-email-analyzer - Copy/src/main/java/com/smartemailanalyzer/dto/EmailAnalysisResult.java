package com.smartemailanalyzer.dto;

import java.util.Collections;
import java.util.List;

/**
 * Result returned by the rule engine after analyzing an email.
 */
public class EmailAnalysisResult {

    private final String cleanedContent;
    private final List<String> tags;
    private final int urgencyScore;

    public EmailAnalysisResult(String cleanedContent, List<String> tags, int urgencyScore) {
        this.cleanedContent = cleanedContent;
        this.tags = tags == null ? Collections.emptyList() : tags;
        this.urgencyScore = urgencyScore;
    }

    public String getCleanedContent() {
        return cleanedContent;
    }

    public List<String> getTags() {
        return tags;
    }

    public int getUrgencyScore() {
        return urgencyScore;
    }

    public String getTagsCsv() {
        return String.join(",", tags);
    }
}
