package com.emailanalyzer.service;

import com.emailanalyzer.entity.Rule;
import java.util.List;

public interface RuleService {
    List<Rule> getAllRules();
    Rule findById(Long id);
    void addRule(Rule rule);
    void updateRule(Rule rule);
    void deleteRule(Long id);
}
