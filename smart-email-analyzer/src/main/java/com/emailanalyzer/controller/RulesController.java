package com.emailanalyzer.controller;

import com.emailanalyzer.entity.Rule;
import com.emailanalyzer.entity.User;
import com.emailanalyzer.service.RuleService;
import com.emailanalyzer.util.SessionHelper;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * CRUD controller for the Rules Management page.
 * Users can add / edit / delete keyword→tag rules and sender rules.
 */
@Controller
@RequestMapping("/rules")
public class RulesController {

    @Autowired
    private RuleService ruleService;

    // ── List all rules ───────────────────────────────────────────

    @GetMapping
    public String listRules(HttpSession session, Model model) {
        if (!SessionHelper.isLoggedIn(session)) return "redirect:/login";

        User user = SessionHelper.getLoggedInUser(session);
        model.addAttribute("rules",     ruleService.getAllRules());
        model.addAttribute("newRule",   new Rule());
        model.addAttribute("user",      user);
        model.addAttribute("pageTitle", "Rule Management");
        return "rules";
    }

    // ── Add new rule ─────────────────────────────────────────────

    @PostMapping("/add")
    public String addRule(
            @RequestParam("type")  String type,
            @RequestParam("value") String value,
            @RequestParam(value = "tag",   required = false) String tag,
            @RequestParam(value = "score", defaultValue = "0") int score,
            HttpSession session,
            RedirectAttributes ra) {

        if (!SessionHelper.isLoggedIn(session)) return "redirect:/login";

        try {
            Rule rule = new Rule(
                type.toUpperCase().trim(),
                value.trim(),
                tag != null ? tag.toUpperCase().trim() : null,
                score
            );
            ruleService.addRule(rule);
            ra.addFlashAttribute("success", "Rule added: [" + type + "] " + value);
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Failed to add rule: " + e.getMessage());
        }

        return "redirect:/rules";
    }

    // ── Show edit form ───────────────────────────────────────────

    @GetMapping("/edit/{id}")
    public String showEditForm(
            @PathVariable("id") Long id,
            HttpSession session,
            Model model) {

        if (!SessionHelper.isLoggedIn(session)) return "redirect:/login";

        Rule rule = ruleService.findById(id);
        if (rule == null) return "redirect:/rules";

        model.addAttribute("editRule",  rule);
        model.addAttribute("rules",     ruleService.getAllRules());
        model.addAttribute("newRule",   new Rule());
        model.addAttribute("user",      SessionHelper.getLoggedInUser(session));
        model.addAttribute("pageTitle", "Edit Rule");
        return "rules";
    }

    // ── Save edited rule ─────────────────────────────────────────

    @PostMapping("/update/{id}")
    public String updateRule(
            @PathVariable("id")    Long   id,
            @RequestParam("type")  String type,
            @RequestParam("value") String value,
            @RequestParam(value = "tag",   required = false) String tag,
            @RequestParam(value = "score", defaultValue = "0") int score,
            HttpSession session,
            RedirectAttributes ra) {

        if (!SessionHelper.isLoggedIn(session)) return "redirect:/login";

        try {
            Rule rule = ruleService.findById(id);
            if (rule == null) {
                ra.addFlashAttribute("error", "Rule not found.");
                return "redirect:/rules";
            }
            rule.setType(type.toUpperCase().trim());
            rule.setValue(value.trim());
            rule.setTag(tag != null ? tag.toUpperCase().trim() : null);
            rule.setScore(score);
            ruleService.updateRule(rule);
            ra.addFlashAttribute("success", "Rule updated successfully.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Failed to update rule: " + e.getMessage());
        }

        return "redirect:/rules";
    }

    // ── Delete rule ──────────────────────────────────────────────

    @GetMapping("/delete/{id}")
    public String deleteRule(
            @PathVariable("id") Long id,
            HttpSession session,
            RedirectAttributes ra) {

        if (!SessionHelper.isLoggedIn(session)) return "redirect:/login";

        try {
            ruleService.deleteRule(id);
            ra.addFlashAttribute("success", "Rule deleted.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Failed to delete rule: " + e.getMessage());
        }

        return "redirect:/rules";
    }
}
