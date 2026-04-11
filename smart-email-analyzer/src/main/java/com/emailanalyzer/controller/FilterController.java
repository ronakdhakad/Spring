package com.emailanalyzer.controller;

import com.emailanalyzer.dao.TagDAO;
import com.emailanalyzer.entity.Email;
import com.emailanalyzer.entity.User;
import com.emailanalyzer.service.EmailService;
import com.emailanalyzer.util.SessionHelper;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Handles email filtering and sorting.
 * Supports:
 *   - Filter by tag  (?tag=URGENT)
 *   - Sort by score  (?sort=score)
 *   - Search subject (?q=keyword)
 */
@Controller
public class FilterController {

    @Autowired private EmailService emailService;
    @Autowired private TagDAO       tagDAO;

    @GetMapping("/filter")
    public String filter(
            @RequestParam(value = "tag",  required = false) String tag,
            @RequestParam(value = "sort", required = false) String sort,
            @RequestParam(value = "q",    required = false) String query,
            HttpSession session,
            Model model) {

        if (!SessionHelper.isLoggedIn(session)) return "redirect:/login";

        User loggedInUser = SessionHelper.getLoggedInUser(session);
        Long userId       = loggedInUser.getId();

        List<Email> emails;

        if ("score".equalsIgnoreCase(sort)) {
            // Sort all emails by urgency score descending
            emails = emailService.getSortedByScore(userId);
        } else if (query != null && !query.isBlank()) {
            // Full-text search in subject
            emails = emailService.searchBySubject(userId, query.trim());
        } else if (tag != null && !tag.isBlank()) {
            // Filter by tag
            emails = emailService.filterByTag(userId, tag.trim().toUpperCase());
        } else {
            // No filter – show all
            emails = emailService.getInboxForUser(userId);
        }

        model.addAttribute("emails",     emails);
        model.addAttribute("allTags",    tagDAO.findAll());
        model.addAttribute("activeTag",  tag);
        model.addAttribute("sortBy",     sort);
        model.addAttribute("query",      query);
        model.addAttribute("user",       loggedInUser);
        model.addAttribute("pageTitle",  "Filter & Sort");

        return "filter";
    }
}
