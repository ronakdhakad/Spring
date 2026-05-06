<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dashboard - Smart Email Analyzer</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css">
</head>
<body>
<div class="page-container">
    <%@ include file="common/header.jspf" %>

    <div class="page-toolbar">
        <div>
            <span class="section-kicker">Dashboard</span>
            <h2>Email insights overview</h2>
        </div>
    </div>

    <div class="dashboard-grid">
        <div class="stat-card">
            <div class="stat-label">Total Emails Today</div>
            <div class="stat-value">${stats.totalEmailsToday}</div>
            <div class="stat-note">Messages processed for the current day</div>
        </div>
        <div class="stat-card">
            <div class="stat-label">Total Urgent Emails</div>
            <div class="stat-value">${stats.totalUrgentEmails}</div>
            <div class="stat-note">High-priority emails based on rules and scoring</div>
        </div>
        <div class="stat-card">
            <div class="stat-label">Most Common Tag</div>
            <div class="stat-value stat-text-value">
                <c:choose>
                    <c:when test="${not empty stats.mostCommonTag}">${stats.mostCommonTag}</c:when>
                    <c:otherwise>N/A</c:otherwise>
                </c:choose>
            </div>
            <div class="stat-note">Most repeated tag among analyzed messages</div>
        </div>
    </div>

    <div class="dashboard-panels">
        <div class="panel">
            <span class="section-kicker">Summary</span>
            <h3>Use the dashboard to spot patterns faster.</h3>
            <p class="muted-text">
                A growing urgent-email count usually means your custom rules are correctly surfacing action-based messages.
                Use the inbox and filter pages to inspect those emails in detail.
            </p>
            <div class="feature-list compact-pills">
                <span class="feature-pill light-pill">Today's Load</span>
                <span class="feature-pill light-pill">Urgent Signals</span>
                <span class="feature-pill light-pill">Top Tag Trends</span>
            </div>
        </div>

        <div class="panel">
            <span class="section-kicker">Quick Actions</span>
            <h3>Jump to the next step</h3>
            <div class="quick-links">
                <a class="quick-link-card" href="${pageContext.request.contextPath}/emails/inbox">
                    <strong>Open Inbox</strong>
                    <span>Review analyzed messages</span>
                </a>
                <a class="quick-link-card" href="${pageContext.request.contextPath}/emails/filter">
                    <strong>Filter Emails</strong>
                    <span>Find emails by tag or score</span>
                </a>
                <a class="quick-link-card" href="${pageContext.request.contextPath}/rules">
                    <strong>Manage Rules</strong>
                    <span>Improve urgency and tagging logic</span>
                </a>
            </div>
        </div>
    </div>
</div>
</body>
</html>
