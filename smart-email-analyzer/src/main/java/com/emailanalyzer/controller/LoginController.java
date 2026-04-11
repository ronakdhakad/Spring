package com.emailanalyzer.controller;

import com.emailanalyzer.entity.User;
import com.emailanalyzer.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Handles user login and logout.
 * Session attribute "loggedInUser" (User) is set on successful login.
 */
@Controller
public class LoginController {

    @Autowired
    private UserService userService;

    /** Show login form */
    @GetMapping({"/", "/login"})
    public String showLoginForm(HttpSession session) {
        // Redirect to inbox if already logged in
        if (session.getAttribute("loggedInUser") != null) {
            return "redirect:/inbox";
        }
        return "login";
    }

    /** Process login form submission */
    @PostMapping("/login")
    public String processLogin(
            @RequestParam("email")    String email,
            @RequestParam("password") String password,
            HttpSession session,
            Model model) {

        // Basic validation
        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            model.addAttribute("error", "Email and password are required.");
            return "login";
        }

        User user = userService.login(email.trim(), password.trim());

        if (user == null) {
            model.addAttribute("error", "Invalid email or password. Please try again.");
            model.addAttribute("email", email);
            return "login";
        }

        // Store user in HTTP session
        session.setAttribute("loggedInUser", user);
        session.setAttribute("loggedInUserId", user.getId());
        return "redirect:/inbox";
    }

    /** Logout: invalidate session and redirect to login */
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login?logout=true";
    }
}
