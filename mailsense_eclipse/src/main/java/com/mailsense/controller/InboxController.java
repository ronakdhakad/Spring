package com.mailsense.controller;

import com.mailsense.dao.EmailDao;
import com.mailsense.dao.UserDao;
import com.mailsense.entity.Email;
import com.mailsense.entity.User;
import com.mailsense.security.SecurityUtils;
import com.mailsense.service.AuditService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/inbox")
@Slf4j
public class InboxController {

    @Autowired private EmailDao     emailDao;
    @Autowired private UserDao      userDao;
    @Autowired private AuditService auditService;

    private static final int PAGE_SIZE = 20;

    @GetMapping
    public String inbox(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false)   String tag,
            Model model) {

        User cu     = SecurityUtils.getCurrentUser(userDao);
        Long userId = cu.getId();

        List<Email> emails = (tag != null && !tag.isBlank())
            ? emailDao.findByUserIdAndTag(userId, tag, page, PAGE_SIZE)
            : emailDao.findByUserId(userId, page, PAGE_SIZE);

        model.addAttribute("emails",        emails);
        model.addAttribute("currentPage",   page);
        model.addAttribute("pageSize",      PAGE_SIZE);
        model.addAttribute("unreadCount",   emailDao.countUnreadByUserId(userId));
        model.addAttribute("criticalCount", emailDao.countByUserIdAndUrgencyLevel(
                                            userId, Email.UrgencyLevel.CRITICAL));
        model.addAttribute("currentTag",    tag);
        model.addAttribute("user",          cu);
        return "inbox/inbox";
    }
}
