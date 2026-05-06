package com.smartemailanalyzer.dto;

/**
 * Form for sender importance rule creation.
 */
public class SenderRuleForm {

    private String senderEmail;
    private Integer scoreBoost;

    public String getSenderEmail() {
        return senderEmail;
    }

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }

    public Integer getScoreBoost() {
        return scoreBoost;
    }

    public void setScoreBoost(Integer scoreBoost) {
        this.scoreBoost = scoreBoost;
    }
}
