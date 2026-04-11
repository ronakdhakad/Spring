/<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c"  uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Dashboard – Smart Email Analyzer</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css">
</head>
<body>

<jsp:include page="nav.jsp"/>

<div class="page-container">

    <div class="page-header">
        <h1>Dashboard</h1>
        <p>Overview for <strong>${user.email}</strong></p>
    </div>

    <!-- ═══════════════════════════════════
         Top Stat Cards
    ════════════════════════════════════════ -->
    <div class="stats-grid">

        <div class="stat-card stat-blue">
            <div class="stat-icon"></div>
            <div class="stat-value">${todayCount}</div>
            <div class="stat-label">Emails Today</div>
        </div>

        <div class="stat-card stat-red">
            <div class="stat-icon"></div>
            <div class="stat-value">${urgentCount}</div>
            <div class="stat-label">Urgent Emails</div>
        </div>

        <div class="stat-card stat-green">
            <div class="stat-icon">🏷</div>
            <div class="stat-value">${mostCommonTag}</div>
            <div class="stat-label">Most Common Tag</div>
        </div>

        <div class="stat-card stat-orange">
            <div class="stat-icon"></div>
            <div class="stat-value">${highRisk.size()}</div>
            <div class="stat-label">High-Risk Senders</div>
        </div>

    </div>

    <!-- ═══════════════════════════════════
         Two-column analytics
    ════════════════════════════════════════ -->
    <div class="two-col-layout">

        <!-- Tag Frequency -->
        <div class="analytics-card">
            <h2>Tag Frequency</h2>
            <c:choose>
                <c:when test="${empty tagFrequency}">
                    <p class="empty-state">No tagged emails yet.</p>
                </c:when>
                <c:otherwise>
                    <table class="analytics-table">
                        <thead>
                            <tr><th>Tag</th><th>Count</th><th>Bar</th></tr>
                        </thead>
                        <tbody>
                        <c:forEach var="entry" items="${tagFrequency}">
                            <tr>
                                <td><span class="tag tag-${entry.key.toLowerCase()}">${entry.key}</span></td>
                                <td><strong>${entry.value}</strong></td>
                                <td>
                                    <div class="bar-container">
                                        <div class="bar-fill tag-bar-${entry.key.toLowerCase()}"
                                             style="width: ${entry.value * 10 > 100 ? 100 : entry.value * 10}%">
                                        </div>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </c:otherwise>
            </c:choose>
        </div>

        <!-- High-Risk Senders -->
        <div class="analytics-card">
            <h2>High-Risk Senders</h2>
            <p class="hint-text">Senders with the most URGENT emails</p>
            <c:choose>
                <c:when test="${empty highRisk}">
                    <p class="empty-state">No urgent emails found.</p>
                </c:when>
                <c:otherwise>
                    <table class="analytics-table">
                        <thead>
                            <tr><th>#</th><th>Sender</th><th>Urgent Emails</th></tr>
                        </thead>
                        <tbody>
                        <c:set var="rank" value="1"/>
                        <c:forEach var="entry" items="${highRisk}">
                            <tr>
                                <td>${rank}</td>
                                <td class="sender-cell">${entry.key}</td>
                                <td>
                                    <span class="score-badge score-high">${entry.value}</span>
                                </td>
                            </tr>
                            <c:set var="rank" value="${rank + 1}"/>
                        </c:forEach>
                        </tbody>
                    </table>
                </c:otherwise>
            </c:choose>
        </div>

    </div>

    <!-- ═══════════════════════════════════
         Quick Actions
    ════════════════════════════════════════ -->
    <div class="quick-actions">
        <h2>Quick Actions</h2>
        <div class="action-buttons">
            <a href="${pageContext.request.contextPath}/filter?tag=URGENT"
               class="btn btn-danger">View Urgent Emails</a>
            <a href="${pageContext.request.contextPath}/filter?sort=score"
               class="btn btn-primary">Sort by Score</a>
            <a href="${pageContext.request.contextPath}/rules"
               class="btn btn-secondary">Manage Rules</a>
            <a href="${pageContext.request.contextPath}/inbox"
               class="btn btn-secondary">Go to Inbox</a>
        </div>
    </div>

</div>

</body>
</html>
