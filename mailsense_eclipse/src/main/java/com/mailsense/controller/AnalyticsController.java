package com.mailsense.controller;

import com.mailsense.dao.EmailDao;
import com.mailsense.dao.UserDao;
import com.mailsense.entity.Email;
import com.mailsense.entity.User;
import com.mailsense.security.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/analytics")
@Slf4j
public class AnalyticsController {

    @Autowired private EmailDao emailDao;
    @Autowired private UserDao  userDao;

    @GetMapping
    public String analytics(Model model) {
        User cu     = SecurityUtils.getCurrentUser(userDao);
        Long userId = cu.getId();

        model.addAttribute("criticalCount", emailDao.countByUserIdAndUrgencyLevel(userId, Email.UrgencyLevel.CRITICAL));
        model.addAttribute("highCount",     emailDao.countByUserIdAndUrgencyLevel(userId, Email.UrgencyLevel.HIGH));
        model.addAttribute("mediumCount",   emailDao.countByUserIdAndUrgencyLevel(userId, Email.UrgencyLevel.MEDIUM));
        model.addAttribute("lowCount",      emailDao.countByUserIdAndUrgencyLevel(userId, Email.UrgencyLevel.LOW));
        model.addAttribute("unreadCount",   emailDao.countUnreadByUserId(userId));
        model.addAttribute("topSenders",    emailDao.findTopSendersByUserId(userId, 5));
        model.addAttribute("user",          cu);
        return "analytics/analytics";
    }
}
