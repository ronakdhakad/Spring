package com.mailsense.service;

import com.mailsense.dao.impl.EmailRulesDaoImpl;
import com.mailsense.entity.Email;
import com.mailsense.entity.EmailRule;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Slf4j
public class TagRuleEngine {

    @Autowired
    private EmailRulesDaoImpl emailRulesDao;

    @Transactional(readOnly = true)
    public void applyRules(Email email, Long userId) {
        List<EmailRule> rules = emailRulesDao.findActiveByUserId(userId);
        for (EmailRule rule : rules) {
            if (matches(rule, email)) {
                email.addTag(rule.getTagToApply());
                log.debug("Tag '{}' applied via rule keyword '{}'",
                    rule.getTagToApply(), rule.getKeyword());
            }
        }
    }

    private boolean matches(EmailRule rule, Email email) {
        String fieldValue = switch (rule.getFieldToMatch()) {
            case SENDER  -> email.getFromAddress();
            case SUBJECT -> email.getSubject();
            case BODY    -> email.getBodyPreview();
        };
        if (StringUtils.isBlank(fieldValue)) return false;
        String kw    = rule.getKeyword().toLowerCase();
        String field = fieldValue.toLowerCase();
        return switch (rule.getOperator()) {
            case CONTAINS    -> field.contains(kw);
            case EQUALS      -> field.equals(kw);
            case STARTS_WITH -> field.startsWith(kw);
            case ENDS_WITH   -> field.endsWith(kw);
        };
    }
}
