package com.mailsense.security;

import com.mailsense.dao.UserDao;
import com.mailsense.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtils {

    private SecurityUtils() {}

    public static String getCurrentUserEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated())
            throw new IllegalStateException("No authenticated user");
        return auth.getName();
    }

    public static User getCurrentUser(UserDao userDao) {
        String email = getCurrentUserEmail();
        return userDao.findByEmail(email)
            .orElseThrow(() -> new IllegalStateException("User not found: " + email));
    }

    public static boolean isAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return false;
        return auth.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
}
