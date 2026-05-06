package com.mailsense.security;

import com.mailsense.service.AuditService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;
import javax.servlet.http.*;
import java.io.IOException;

@Component("customLogoutSuccessHandler")
@Slf4j
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {

    @Autowired private AuditService auditService;

    @Override
    public void onLogoutSuccess(HttpServletRequest req,
                                HttpServletResponse res,
                                Authentication auth) throws IOException {
        if (auth != null) {
            log.info("Logout: {}", auth.getName());
            auditService.log(auth.getName(), "LOGOUT", "User logged out", req.getRemoteAddr());
        }
        res.sendRedirect(req.getContextPath() + "/auth/login?logout=true");
    }
}
