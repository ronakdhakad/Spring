package com.emailanalyzer.controller;

import com.emailanalyzer.entity.User;
import com.emailanalyzer.service.EmailService;
import com.emailanalyzer.util.SessionHelper;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

/**
 * Dashboard controller – provides email analytics for the logged-in user.
 */
@Controller
public class DashboardController {
//	igaeamriztaqrbsy
//	niuhktwsmtrlyopl
    @Autowired
    private EmailService emailService;

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {

        if (!SessionHelper.isLoggedIn(session)) return "redirect:/login";

        User loggedInUser = SessionHelper.getLoggedInUser(session);
        Long userId       = loggedInUser.getId();

        // ── Stats ────────────────────────────────────────────────
        int todayCount   = emailService.countTodayEmails(userId);
        int urgentCount  = emailService.countUrgentEmails(userId);

        Map<String, Long> tagFrequency  = emailService.getTagFrequency(userId);
        Map<String, Long> highRisk      = emailService.getHighRiskSenders(userId);

        // Most common tag
        String mostCommonTag = tagFrequency.isEmpty()
                ? "None"
                : tagFrequency.entrySet().iterator().next().getKey();

        model.addAttribute("todayCount",    todayCount);
        model.addAttribute("urgentCount",   urgentCount);
        model.addAttribute("tagFrequency",  tagFrequency);
        model.addAttribute("highRisk",      highRisk);
        model.addAttribute("mostCommonTag", mostCommonTag);
        model.addAttribute("user",          loggedInUser);
        model.addAttribute("pageTitle",     "Dashboard");

        return "dashboard";
    }
}
