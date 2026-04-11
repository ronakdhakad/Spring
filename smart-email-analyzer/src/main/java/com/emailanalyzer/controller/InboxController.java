package com.emailanalyzer.controller;

import com.emailanalyzer.entity.Email;
import com.emailanalyzer.entity.User;
import com.emailanalyzer.service.EmailService;
import com.emailanalyzer.util.SessionHelper;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * Displays the inbox – all emails for the logged-in user.
 */
@Controller
public class InboxController {

    @Autowired
    private EmailService emailService;

    @GetMapping("/inbox")
    public String inbox(HttpSession session, Model model) {

        // Guard: must be logged in
        if (!SessionHelper.isLoggedIn(session)) {
            return "redirect:/login";
        }

        User loggedInUser = SessionHelper.getLoggedInUser(session);
        Long userId       = loggedInUser.getId();

        List<Email> emails = emailService.getInboxForUser(userId);

        model.addAttribute("emails", emails);
        model.addAttribute("user",   loggedInUser);
        model.addAttribute("pageTitle", "Inbox");

        return "inbox";
    }
}
