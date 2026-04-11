package com.emailanalyzer.controller;

import com.emailanalyzer.entity.Email;
import com.emailanalyzer.entity.User;
import com.emailanalyzer.service.EmailService;
import com.emailanalyzer.util.SessionHelper;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Handles viewing a single email, manually updating urgency score,
 * and replying to an email.
 */
@Controller
@RequestMapping("/email")
public class EmailDetailController {

    @Autowired
    private EmailService emailService;

    // ── View email detail ────────────────────────────────────────

    @GetMapping("/{id}")
    public String viewEmail(
            @PathVariable("id") Long emailId,
            HttpSession session,
            Model model) {

        if (!SessionHelper.isLoggedIn(session)) return "redirect:/login";

        Long userId = SessionHelper.getLoggedInUserId(session);
        Email email = emailService.findById(emailId);

        // Security: ensure this email belongs to the logged-in user
        if (email == null || !email.getUser().getId().equals(userId)) {
            return "redirect:/inbox";
        }

        model.addAttribute("email",  email);
        model.addAttribute("user",   SessionHelper.getLoggedInUser(session));
        model.addAttribute("pageTitle", "Email Detail");
        return "emailDetail";
    }

    // ── Manually update urgency score ────────────────────────────

    @PostMapping("/{id}/updateScore")
    public String updateScore(
            @PathVariable("id")    Long emailId,
            @RequestParam("score") int  newScore,
            HttpSession session,
            RedirectAttributes ra) {

        if (!SessionHelper.isLoggedIn(session)) return "redirect:/login";

        Long userId = SessionHelper.getLoggedInUserId(session);
        Email email = emailService.findById(emailId);

        if (email == null || !email.getUser().getId().equals(userId)) {
            return "redirect:/inbox";
        }

        try {
            emailService.updateScore(emailId, newScore);
            ra.addFlashAttribute("success", "Urgency score updated to " + newScore);
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Failed to update score: " + e.getMessage());
        }

        return "redirect:/email/" + emailId;
    }

    // ── Reply to email ───────────────────────────────────────────

    @PostMapping("/{id}/reply")
    public String replyToEmail(
            @PathVariable("id")       Long   emailId,
            @RequestParam("replyBody") String replyBody,
            HttpSession session,
            RedirectAttributes ra) {

        if (!SessionHelper.isLoggedIn(session)) return "redirect:/login";

        Long userId = SessionHelper.getLoggedInUserId(session);
        Email email = emailService.findById(emailId);

        if (email == null || !email.getUser().getId().equals(userId)) {
            return "redirect:/inbox";
        }

        if (replyBody == null || replyBody.isBlank()) {
            ra.addFlashAttribute("error", "Reply body cannot be empty.");
            return "redirect:/email/" + emailId;
        }

        try {
            emailService.replyToEmail(emailId, replyBody.trim(), userId);
            ra.addFlashAttribute("success", "Reply sent successfully to " + email.getSender());
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Failed to send reply: " + e.getMessage());
        }

        return "redirect:/email/" + emailId;
    }
}
