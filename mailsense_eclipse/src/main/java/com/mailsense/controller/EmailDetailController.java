package com.mailsense.controller;

import com.mailsense.dao.EmailDao;
import com.mailsense.dao.UserDao;
import com.mailsense.dto.SendEmailDto;
import com.mailsense.entity.Email;
import com.mailsense.entity.SentEmail;
import com.mailsense.entity.User;
import com.mailsense.security.SecurityUtils;
import com.mailsense.service.AuditService;
import com.mailsense.service.EmailSendService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.LocalDateTime;

@Controller
@RequestMapping("/emails")
@Slf4j
public class EmailDetailController {

    @Autowired private EmailDao         emailDao;
    @Autowired private UserDao          userDao;
    @Autowired private EmailSendService sendService;
    @Autowired private AuditService     auditService;

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        User cu = SecurityUtils.getCurrentUser(userDao);

        Email email = emailDao.findById(id, cu.getId())
            .orElseThrow(() -> new RuntimeException("Email not found or access denied"));

        if (!email.isRead()) {
            email.setRead(true);
            emailDao.update(email);
        }

        model.addAttribute("email",      email);
        model.addAttribute("replyDto",   replyDto(email));
        model.addAttribute("forwardDto", forwardDto(email));
        model.addAttribute("newDto",     new SendEmailDto());
        model.addAttribute("user",       cu);
        return "inbox/email-detail";
    }

    @PostMapping("/send")
    public String send(@ModelAttribute SendEmailDto dto,
                       RedirectAttributes ra) {
        User cu = SecurityUtils.getCurrentUser(userDao);
        try {
            sendService.send(dto, cu);
            ra.addFlashAttribute("sendSuccess", true);
            return dto.getInReplyToEmailId() != null
                ? "redirect:/emails/" + dto.getInReplyToEmailId()
                : "redirect:/inbox";
        } catch (Exception e) {
            log.error("Send failed: {}", e.getMessage());
            ra.addFlashAttribute("sendError", "Send failed: " + e.getMessage());
            return "redirect:/inbox";
        }
    }

    @PostMapping("/{id}/snooze")
    public String snooze(@PathVariable Long id,
                         @RequestParam String snoozeUntil,
                         RedirectAttributes ra) {
        User cu = SecurityUtils.getCurrentUser(userDao);
        emailDao.findById(id, cu.getId()).ifPresent(e -> {
            e.setSnoozedUntil(LocalDateTime.parse(snoozeUntil));
            emailDao.update(e);
            auditService.logById(cu.getId(), "SNOOZE_SET",
                "Snoozed until " + snoozeUntil + ": " + e.getSubject());
        });
        ra.addFlashAttribute("snoozeSuccess", true);
        return "redirect:/inbox";
    }

    @PostMapping("/{id}/unsnooze")
    public String unsnooze(@PathVariable Long id, RedirectAttributes ra) {
        User cu = SecurityUtils.getCurrentUser(userDao);
        emailDao.findById(id, cu.getId()).ifPresent(e -> {
            e.setSnoozedUntil(null);
            emailDao.update(e);
        });
        return "redirect:/inbox";
    }

    private SendEmailDto replyDto(Email e) {
        return SendEmailDto.builder()
            .toAddress(e.getFromAddress())
            .subject("Re: " + e.getSubject())
            .sendType(SentEmail.SendType.REPLY)
            .inReplyToEmailId(e.getId())
            .inReplyToMessageId(e.getMessageId())
            .build();
    }

    private SendEmailDto forwardDto(Email e) {
        return SendEmailDto.builder()
            .subject("Fwd: " + e.getSubject())
            .sendType(SentEmail.SendType.FORWARD)
            .inReplyToEmailId(e.getId())
            .originalBody(e.getFullBody())
            .build();
    }
}
