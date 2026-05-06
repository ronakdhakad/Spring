package com.mailsense.controller;

import com.mailsense.dto.RegisterDto;
import com.mailsense.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import javax.validation.Valid;

@Controller
@RequestMapping("/auth")
@Slf4j
public class AuthController {

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String loginPage(
            @RequestParam(required = false) String error,
            @RequestParam(required = false) String logout,
            @RequestParam(required = false) String session,
            Model model) {
        if ("true".equals(error))       model.addAttribute("loginError",    true);
        if ("true".equals(logout))      model.addAttribute("logoutSuccess", true);
        if ("expired".equals(session))  model.addAttribute("sessionExpired",true);
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("registerDto", new RegisterDto());
        return "auth/register";
    }

    @PostMapping("/register")
    public String doRegister(
            @Valid @ModelAttribute("registerDto") RegisterDto dto,
            BindingResult result,
            RedirectAttributes redirectAttrs,
            Model model) {

        if (result.hasErrors()) return "auth/register";

        if (!dto.passwordsMatch()) {
            model.addAttribute("pwdMismatch", true);
            return "auth/register";
        }

        try {
            userService.register(dto);
            redirectAttrs.addFlashAttribute("registered", true);
            return "redirect:/auth/login";
        } catch (IllegalArgumentException e) {
            model.addAttribute("registerError", e.getMessage());
            return "auth/register";
        }
    }

    @GetMapping("/access-denied")
    public String accessDenied() {
        return "access-denied";
    }
}
