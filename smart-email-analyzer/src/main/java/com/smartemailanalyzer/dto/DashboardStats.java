package com.smartemailanalyzer.dto;

/**
 * Dashboard summary values.
 */
public class DashboardStats {

    private long totalEmailsToday;
    private long totalUrgentEmails;
    private String mostCommonTag;

    public long getTotalEmailsToday() {
        return totalEmailsToday;
    }

    public void setTotalEmailsToday(long totalEmailsToday) {
        this.totalEmailsToday = totalEmailsToday;
    }

    public long getTotalUrgentEmails() {
        return totalUrgentEmails;
    }

    public void setTotalUrgentEmails(long totalUrgentEmails) {
        this.totalUrgentEmails = totalUrgentEmails;
    }

    public String getMostCommonTag() {
        return mostCommonTag;
    }

    public void setMostCommonTag(String mostCommonTag) {
        this.mostCommonTag = mostCommonTag;
    }
}
