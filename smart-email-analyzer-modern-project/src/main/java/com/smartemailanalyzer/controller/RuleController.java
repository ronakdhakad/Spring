package com.smartemailanalyzer.controller;

import com.smartemailanalyzer.dto.KeywordRuleForm;
import com.smartemailanalyzer.dto.SenderRuleForm;
import com.smartemailanalyzer.service.RuleService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

/**
 * Rule management screen: keyword rules, sender rules, and score weights.
 */
@Controller
@RequestMapping("/rules")
public class RuleController {

    private final RuleService ruleService;

    public RuleController(RuleService ruleService) {
        this.ruleService = ruleService;
    }

    @GetMapping
    public String rulesPage(Model model, HttpSession session) {
        Long userId = currentUserId(session);
        model.addAttribute("keywordRules", ruleService.getKeywordRules(userId));
        model.addAttribute("senderRules", ruleService.getSenderRules(userId));
        model.addAttribute("scoreWeights", ruleService.getScoreWeights(userId));
        model.addAttribute("keywordRuleForm", new KeywordRuleForm());
        model.addAttribute("senderRuleForm", new SenderRuleForm());
        model.addAttribute("currentPage", "rules");
        return "rules";
    }

    @PostMapping("/keyword")
    public String addKeywordRule(@ModelAttribute KeywordRuleForm keywordRuleForm,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        if (isBlank(keywordRuleForm.getKeyword()) || isBlank(keywordRuleForm.getTag())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Keyword and tag are required.");
            return "redirect:/rules";
        }

        ruleService.addKeywordRule(currentUserId(session), keywordRuleForm);
        redirectAttributes.addFlashAttribute("successMessage", "Keyword rule added.");
        return "redirect:/rules";
    }

    @PostMapping("/keyword/{ruleId}/delete")
    public String deleteKeywordRule(@PathVariable Long ruleId,
                                    HttpSession session,
                                    RedirectAttributes redirectAttributes) {
        ruleService.deleteKeywordRule(currentUserId(session), ruleId);
        redirectAttributes.addFlashAttribute("successMessage", "Keyword rule removed.");
        return "redirect:/rules";
    }

    @PostMapping("/sender")
    public String addSenderRule(@ModelAttribute SenderRuleForm senderRuleForm,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        if (isBlank(senderRuleForm.getSenderEmail())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Sender email is required.");
            return "redirect:/rules";
        }

        ruleService.addOrUpdateSenderRule(currentUserId(session), senderRuleForm);
        redirectAttributes.addFlashAttribute("successMessage", "Sender rule saved.");
        return "redirect:/rules";
    }

    @PostMapping("/sender/{ruleId}/delete")
    public String deleteSenderRule(@PathVariable Long ruleId,
                                   HttpSession session,
                                   RedirectAttributes redirectAttributes) {
        ruleService.deleteSenderRule(currentUserId(session), ruleId);
        redirectAttributes.addFlashAttribute("successMessage", "Sender rule removed.");
        return "redirect:/rules";
    }

    @PostMapping("/weights")
    public String updateWeights(@RequestParam Map<String, String> formValues,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        ruleService.updateWeights(currentUserId(session), formValues);
        redirectAttributes.addFlashAttribute("successMessage", "Score weights updated.");
        return "redirect:/rules";
    }

    private Long currentUserId(HttpSession session) {
        return Long.valueOf(session.getAttribute("loggedInUserId").toString());
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isBlank();
    }
}
