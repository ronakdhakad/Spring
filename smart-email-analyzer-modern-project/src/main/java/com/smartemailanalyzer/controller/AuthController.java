package com.smartemailanalyzer.controller;

import com.smartemailanalyzer.dto.LoginForm;
import com.smartemailanalyzer.dto.RegistrationForm;
import com.smartemailanalyzer.entity.User;
import com.smartemailanalyzer.service.EmailFetchService;
import com.smartemailanalyzer.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Handles registration, login, and logout.
 */
@Controller
public class AuthController {

    private final UserService userService;
    private final EmailFetchService emailFetchService;

    public AuthController(UserService userService, EmailFetchService emailFetchService) {
        this.userService = userService;
        this.emailFetchService = emailFetchService;
    }

    @GetMapping("/login")
    public String loginPage(Model model, HttpSession session) {
        if (session.getAttribute("loggedInUserId") != null) {
            return "redirect:/emails/inbox";
        }
        model.addAttribute("loginForm", new LoginForm());
        return "login";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute("loginForm") LoginForm form,
                        Model model,
                        HttpSession session,
                        RedirectAttributes redirectAttributes) {
        if (isBlank(form.getEmail()) || isBlank(form.getPassword())) {
            model.addAttribute("errorMessage", "Email and password are required.");
            return "login";
        }

        User user = userService.authenticate(form.getEmail(), form.getPassword()).orElse(null);
        if (user == null) {
            model.addAttribute("errorMessage", "Invalid email or password.");
            return "login";
        }

        userService.updateLoggedInStatus(user.getId(), true);
        session.setAttribute("loggedInUserId", user.getId());
        session.setAttribute("loggedInUserName", user.getName());
        session.setAttribute("loggedInUserEmail", user.getEmail());
        session.setMaxInactiveInterval(30 * 60);

        try {
            int syncedCount = emailFetchService.fetchNewEmailsForUser(user.getId());
            redirectAttributes.addFlashAttribute("successMessage",
                    "Login successful. Inbox synchronized. New emails fetched: " + syncedCount);
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Login successful, but IMAP sync failed: " + ex.getMessage());
        }

        return "redirect:/emails/inbox";
    }

    @GetMapping("/register")
    public String registerPage(Model model, HttpSession session) {
        if (session.getAttribute("loggedInUserId") != null) {
            return "redirect:/emails/inbox";
        }
        model.addAttribute("registrationForm", new RegistrationForm());
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute("registrationForm") RegistrationForm form,
                           Model model,
                           RedirectAttributes redirectAttributes) {
        if (isBlank(form.getName()) || isBlank(form.getEmail()) || isBlank(form.getPassword())
                || isBlank(form.getGoogleAppPassword())) {
            model.addAttribute("errorMessage", "All registration fields are required.");
            return "register";
        }

        if (!form.getEmail().contains("@")) {
            model.addAttribute("errorMessage", "Please enter a valid email address.");
            return "register";
        }

        try {
            userService.register(form);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Registration completed. Please log in with your email and password.");
            return "redirect:/login";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return "register";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        Object userId = session.getAttribute("loggedInUserId");
        if (userId != null) {
            userService.updateLoggedInStatus(Long.valueOf(userId.toString()), false);
        }
        session.invalidate();
        redirectAttributes.addFlashAttribute("successMessage", "Logged out successfully.");
        return "redirect:/login";
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isBlank();
    }
}
