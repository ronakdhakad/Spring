package com.smartemailanalyzer.dto;

/**
 * Form for keyword rule creation.
 */
public class KeywordRuleForm {

    private String keyword;
    private String tag;
    private Integer scoreBoost;

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Integer getScoreBoost() {
        return scoreBoost;
    }

    public void setScoreBoost(Integer scoreBoost) {
        this.scoreBoost = scoreBoost;
    }
}
