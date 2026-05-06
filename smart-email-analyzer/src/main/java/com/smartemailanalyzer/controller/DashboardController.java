package com.smartemailanalyzer.controller;

import com.smartemailanalyzer.service.DashboardService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Dashboard summary page.
 */
@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping
    public String dashboard(Model model, HttpSession session) {
        Long userId = Long.valueOf(session.getAttribute("loggedInUserId").toString());
        model.addAttribute("stats", dashboardService.getDashboardStats(userId));
        model.addAttribute("currentPage", "dashboard");
        return "dashboard";
    }
}
