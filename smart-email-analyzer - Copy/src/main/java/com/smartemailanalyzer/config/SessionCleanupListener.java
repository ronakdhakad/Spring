package com.smartemailanalyzer.config;

import com.smartemailanalyzer.service.UserService;
import jakarta.servlet.annotation.WebListener;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * Marks the user as logged out when the HTTP session expires.
 */
@WebListener
public class SessionCleanupListener implements HttpSessionListener {

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        // No-op
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        Object userId = se.getSession().getAttribute("loggedInUserId");
        if (userId == null) {
            return;
        }

        WebApplicationContext context = WebApplicationContextUtils
                .getWebApplicationContext(se.getSession().getServletContext());
        if (context != null) {
            UserService userService = context.getBean(UserService.class);
            userService.updateLoggedInStatus(Long.valueOf(userId.toString()), false);
        }
    }
}
