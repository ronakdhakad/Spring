package com.smartemailanalyzer.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Redirects the root URL based on login state.
 */
@Controller
public class HomeController {

    @GetMapping("/")
    public String home(HttpSession session) {
        return session.getAttribute("loggedInUserId") == null
                ? "redirect:/login"
                : "redirect:/emails/inbox";
    }
}
