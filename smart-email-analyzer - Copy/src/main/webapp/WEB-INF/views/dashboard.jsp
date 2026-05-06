<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Dashboard - Smart Email Analyzer</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css">
</head>
<body>
<div class="page-container">
    <%@ include file="common/header.jspf" %>

    <div class="page-toolbar">
        <div>
            <h2>Dashboard</h2>
            <p class="muted-text">Quick summary of the current user’s analyzed inbox.</p>
        </div>
    </div>

    <div class="dashboard-grid">
        <div class="stat-card">
            <div class="stat-label">Total Emails Today</div>
            <div class="stat-value">${stats.totalEmailsToday}</div>
        </div>
        <div class="stat-card">
            <div class="stat-label">Total Urgent Emails</div>
            <div class="stat-value">${stats.totalUrgentEmails}</div>
        </div>
        <div class="stat-card">
            <div class="stat-label">Most Common Tag</div>
            <div class="stat-value">${stats.mostCommonTag}</div>
        </div>
    </div>

    <div class="panel section-space">
        <h3>How the flow works</h3>
        <p class="flow-text">Gmail Inbox → IMAP → Spring Backend → Email Parsing → Rule Engine → Urgency Score → Database → JSP UI</p>
    </div>

    <%@ include file="common/footer.jspf" %>
</div>
</body>
</html>
