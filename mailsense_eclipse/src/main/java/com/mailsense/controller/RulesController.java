package com.mailsense.controller;

import com.mailsense.dao.UserDao;
import com.mailsense.dao.impl.EmailRulesDaoImpl;
import com.mailsense.entity.EmailRule;
import com.mailsense.entity.User;
import com.mailsense.security.SecurityUtils;
import com.mailsense.service.AuditService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;

@Controller
@RequestMapping("/rules")
@Slf4j
public class RulesController {

    @Autowired private EmailRulesDaoImpl rulesDao;
    @Autowired private UserDao           userDao;
    @Autowired private AuditService      auditService;

    @GetMapping
    public String list(Model model) {
        User cu = SecurityUtils.getCurrentUser(userDao);
        List<EmailRule> rules = rulesDao.findActiveByUserId(cu.getId());
        model.addAttribute("rules",     rules);
        model.addAttribute("fields",    EmailRule.MatchField.values());
        model.addAttribute("operators", EmailRule.MatchOperator.values());
        model.addAttribute("user",      cu);
        return "rules/rules";
    }

    @PostMapping
    public String create(
            @RequestParam String fieldToMatch,
            @RequestParam String operator,
            @RequestParam String keyword,
            @RequestParam String tagToApply,
            @RequestParam(defaultValue = "1") int priority,
            RedirectAttributes ra) {

        User cu = SecurityUtils.getCurrentUser(userDao);
        EmailRule rule = new EmailRule();
        rule.setUser(cu);
        rule.setFieldToMatch(EmailRule.MatchField.valueOf(fieldToMatch));
        rule.setOperator(EmailRule.MatchOperator.valueOf(operator));
        rule.setKeyword(keyword.trim().toLowerCase());
        rule.setTagToApply(tagToApply.trim().toUpperCase());
        rule.setPriority(priority);
        rule.setActive(true);
        rulesDao.save(rule);
        auditService.logById(cu.getId(), "RULE_CREATED",
            "if " + fieldToMatch + " " + operator + " '" + keyword + "' => " + tagToApply);
        ra.addFlashAttribute("ruleSuccess", "Rule created.");
        return "redirect:/rules";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        User cu = SecurityUtils.getCurrentUser(userDao);
        EmailRule rule = rulesDao.findByIdAndUserId(id, cu.getId())
            .orElseThrow(() -> new RuntimeException("Rule not found"));
        model.addAttribute("rule",      rule);
        model.addAttribute("fields",    EmailRule.MatchField.values());
        model.addAttribute("operators", EmailRule.MatchOperator.values());
        model.addAttribute("user",      cu);
        return "rules/edit-rule";
    }

    @PostMapping("/{id}/update")
    public String update(
            @PathVariable Long id,
            @RequestParam String fieldToMatch,
            @RequestParam String operator,
            @RequestParam String keyword,
            @RequestParam String tagToApply,
            @RequestParam(defaultValue = "1") int priority,
            RedirectAttributes ra) {

        User cu = SecurityUtils.getCurrentUser(userDao);
        rulesDao.findByIdAndUserId(id, cu.getId()).ifPresent(rule -> {
            rule.setFieldToMatch(EmailRule.MatchField.valueOf(fieldToMatch));
            rule.setOperator(EmailRule.MatchOperator.valueOf(operator));
            rule.setKeyword(keyword.trim().toLowerCase());
            rule.setTagToApply(tagToApply.trim().toUpperCase());
            rule.setPriority(priority);
            rulesDao.update(rule);
            auditService.logById(cu.getId(), "RULE_UPDATED", "Rule id=" + id);
        });
        ra.addFlashAttribute("ruleSuccess", "Rule updated.");
        return "redirect:/rules";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        User cu = SecurityUtils.getCurrentUser(userDao);
        rulesDao.delete(id, cu.getId());
        auditService.logById(cu.getId(), "RULE_DELETED", "Rule id=" + id);
        ra.addFlashAttribute("ruleSuccess", "Rule deleted.");
        return "redirect:/rules";
    }
}
