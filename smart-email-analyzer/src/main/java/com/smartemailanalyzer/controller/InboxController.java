package com.smartemailanalyzer.controller;

import com.smartemailanalyzer.entity.EmailMessage;
import com.smartemailanalyzer.service.EmailFetchService;
import com.smartemailanalyzer.service.EmailService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Inbox pages and email detail pages.
 */
@Controller
@RequestMapping("/emails")
public class InboxController {

    private final EmailService emailService;
    private final EmailFetchService emailFetchService;

    public InboxController(EmailService emailService, EmailFetchService emailFetchService) {
        this.emailService = emailService;
        this.emailFetchService = emailFetchService;
    }

    @GetMapping("/inbox")
    public String inbox(Model model, HttpSession session) {
        Long userId = currentUserId(session);
        model.addAttribute("emails", emailService.getInboxEmails(userId));
        model.addAttribute("currentPage", "inbox");
        return "inbox";
    }

    @GetMapping("/filter")
    public String filter(@RequestParam(value = "tag", required = false) String tag,
                         @RequestParam(value = "sort", defaultValue = "scoreHigh") String sort,
                         Model model,
                         HttpSession session) {
        Long userId = currentUserId(session);
        model.addAttribute("emails", emailService.getFilteredEmails(userId, tag, sort));
        model.addAttribute("availableTags", emailService.getAvailableTags(userId));
        model.addAttribute("selectedTag", tag == null ? "" : tag);
        model.addAttribute("selectedSort", sort);
        model.addAttribute("currentPage", "filter");
        return "filter";
    }

    @GetMapping("/{emailId}")
    public String detail(@PathVariable("emailId") Long emailId,
                         Model model,
                         HttpSession session,
                         RedirectAttributes redirectAttributes) {
        Long userId = currentUserId(session);
        EmailMessage emailMessage = emailService.getEmailDetail(emailId, userId);
        if (emailMessage == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Email not found.");
            return "redirect:/emails/inbox";
        }
        model.addAttribute("emailMessage", emailMessage);
        model.addAttribute("currentPage", "inbox");
        return "emailDetail";
    }

    @GetMapping("/refresh")
    public String refresh(HttpSession session, RedirectAttributes redirectAttributes) {
        Long userId = currentUserId(session);
        try {
            int syncedCount = emailFetchService.fetchNewEmailsForUser(userId);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Inbox refreshed successfully. New emails fetched: " + syncedCount);
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Unable to refresh inbox: " + ex.getMessage());
        }
        return "redirect:/emails/inbox";
    }

    private Long currentUserId(HttpSession session) {
        return Long.valueOf(session.getAttribute("loggedInUserId").toString());
    }
}
