package com.mailsense.security;

import com.mailsense.service.AuditService;
import com.mailsense.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import javax.servlet.http.*;
import java.io.IOException;

@Component("customAuthSuccessHandler")
@Slf4j
public class CustomAuthSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired private AuditService auditService;
    @Autowired private UserService  userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest req,
                                        HttpServletResponse res,
                                        Authentication auth) throws IOException {
        String email = auth.getName();
        String ip    = req.getHeader("X-Forwarded-For") != null
                       ? req.getHeader("X-Forwarded-For") : req.getRemoteAddr();

        log.info("Login success: {} from {}", email, ip);
        userService.resetFailedLoginAttempts(email);
        auditService.log(email, "LOGIN_SUCCESS", "Login from " + ip, ip);
        res.sendRedirect(req.getContextPath() + "/inbox");
    }
}
