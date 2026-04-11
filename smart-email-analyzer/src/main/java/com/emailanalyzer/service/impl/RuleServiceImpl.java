package com.emailanalyzer.service.impl;

import com.emailanalyzer.dao.RuleDAO;
import com.emailanalyzer.entity.Rule;
import com.emailanalyzer.service.RuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class RuleServiceImpl implements RuleService {

    @Autowired
    private RuleDAO ruleDAO;

    @Override
    @Transactional(readOnly = true)
    public List<Rule> getAllRules() {
        return ruleDAO.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Rule findById(Long id) {
        return ruleDAO.findById(id);
    }

    @Override
    public void addRule(Rule rule) {
        if (rule.getValue() == null || rule.getValue().isBlank())
            throw new IllegalArgumentException("Rule value cannot be empty.");
        if (rule.getType() == null)
            throw new IllegalArgumentException("Rule type cannot be null.");
        ruleDAO.save(rule);
    }

    @Override
    public void updateRule(Rule rule) {
        ruleDAO.update(rule);
    }

    @Override
    public void deleteRule(Long id) {
        ruleDAO.delete(id);
    }
}
