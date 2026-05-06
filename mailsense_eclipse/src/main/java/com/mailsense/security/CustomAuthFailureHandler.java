package com.mailsense.security;

import com.mailsense.service.AuditService;
import com.mailsense.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import javax.servlet.http.*;
import java.io.IOException;

@Component("customAuthFailureHandler")
@Slf4j
public class CustomAuthFailureHandler implements AuthenticationFailureHandler {

    @Autowired private AuditService auditService;
    @Autowired private UserService  userService;

    private static final int MAX_ATTEMPTS = 5;

    @Override
    public void onAuthenticationFailure(HttpServletRequest req,
                                        HttpServletResponse res,
                                        AuthenticationException ex) throws IOException {
        String email = req.getParameter("email");
        String ip    = req.getRemoteAddr();
        log.warn("Login failed for {} from {}", email, ip);

        if (email != null && !email.isBlank()) {
            int attempts = userService.incrementFailedLoginAttempts(email);
            if (attempts >= MAX_ATTEMPTS) {
                userService.lockAccount(email, 30);
                log.warn("Account locked after {} attempts: {}", attempts, email);
            }
        }
        auditService.logAnonymous("LOGIN_FAILURE", "Failed login for: " + email, ip);
        res.sendRedirect(req.getContextPath() + "/auth/login?error=true");
    }
}
