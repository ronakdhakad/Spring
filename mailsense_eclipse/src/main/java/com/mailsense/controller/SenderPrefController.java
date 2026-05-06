package com.mailsense.controller;

import com.mailsense.dao.UserDao;
import com.mailsense.dao.impl.SenderPrefDaoImpl;
import com.mailsense.entity.SenderPreference;
import com.mailsense.entity.User;
import com.mailsense.security.SecurityUtils;
import com.mailsense.service.AuditService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;

@Controller
@RequestMapping("/senders")
@Slf4j
public class SenderPrefController {

    @Autowired private SenderPrefDaoImpl prefDao;
    @Autowired private UserDao           userDao;
    @Autowired private AuditService      auditService;

    @GetMapping
    public String list(Model model) {
        User cu = SecurityUtils.getCurrentUser(userDao);
        List<SenderPreference> prefs = prefDao.findByUserId(cu.getId());
        model.addAttribute("prefs",     prefs);
        model.addAttribute("prefTypes", SenderPreference.PrefType.values());
        model.addAttribute("user",      cu);
        return "senders/sender-prefs";
    }

    @PostMapping
    public String add(
            @RequestParam String senderEmail,
            @RequestParam String prefType,
            RedirectAttributes ra) {

        User cu = SecurityUtils.getCurrentUser(userDao);

        if (prefDao.findByUserIdAndEmail(cu.getId(), senderEmail.trim()).isPresent()) {
            ra.addFlashAttribute("prefError", "Preference for this sender already exists.");
            return "redirect:/senders";
        }

        SenderPreference.PrefType type = SenderPreference.PrefType.valueOf(prefType);
        int modifier = (type == SenderPreference.PrefType.WHITELIST) ? 20 : -20;

        SenderPreference pref = SenderPreference.builder()
            .user(cu)
            .senderEmail(senderEmail.trim().toLowerCase())
            .prefType(type)
            .scoreModifier(modifier)
            .build();

        prefDao.save(pref);
        auditService.logById(cu.getId(),
            type == SenderPreference.PrefType.WHITELIST ? "SENDER_WHITELISTED" : "SENDER_BLACKLISTED",
            "Sender: " + senderEmail);
        ra.addFlashAttribute("prefSuccess", senderEmail + " added to " + prefType);
        return "redirect:/senders";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        User cu = SecurityUtils.getCurrentUser(userDao);
        prefDao.delete(id, cu.getId());
        auditService.logById(cu.getId(), "SENDER_PREF_DELETED", "id=" + id);
        ra.addFlashAttribute("prefSuccess", "Preference removed.");
        return "redirect:/senders";
    }
}
