package com.emailanalyzer.util;

import com.emailanalyzer.entity.User;
import jakarta.servlet.http.HttpSession;

/**
 * Utility methods for session management.
 */
public class SessionHelper {

    private SessionHelper() {}

    /**
     * Returns the currently logged-in user, or null if not authenticated.
     */
    public static User getLoggedInUser(HttpSession session) {
        if (session == null) return null;
        return (User) session.getAttribute("loggedInUser");
    }

    /**
     * Returns the logged-in user's ID, or null.
     */
    public static Long getLoggedInUserId(HttpSession session) {
        User user = getLoggedInUser(session);
        return user != null ? user.getId() : null;
    }

    /**
     * Returns true if a user is currently authenticated.
     */
    public static boolean isLoggedIn(HttpSession session) {
        return getLoggedInUser(session) != null;
    }
}
