<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Inbox - Smart Email Analyzer</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css">
</head>
<body>
<div class="page-container">
    <%@ include file="common/header.jspf" %>

    <div class="page-toolbar">
        <div>
            <span class="section-kicker">Inbox</span>
            <h2>Analyzed email messages</h2>
            <p class="muted-text">Review sender, subject, tags, and urgency score for the current user.</p>
        </div>
        <div class="toolbar-actions">
            <a class="btn btn-secondary" href="${pageContext.request.contextPath}/emails/refresh">Refresh Inbox</a>
            <a class="btn btn-outline" href="${pageContext.request.contextPath}/emails/filter">Filter &amp; Sort</a>
        </div>
    </div>

    <div class="panel page-banner">
        <div>
            <h3>Quick review</h3>
            <p class="muted-text">Open any subject line to view the cleaned content, tags, and score breakdown-ready details.</p>
        </div>
        <div class="feature-list compact-pills">
            <span class="feature-pill light-pill">Sender</span>
            <span class="feature-pill light-pill">Tags</span>
            <span class="feature-pill light-pill">Urgency Score</span>
        </div>
    </div>

    <div class="panel table-panel">
        <c:choose>
            <c:when test="${empty emails}">
                <div class="empty-state">
                    <h3>No emails found</h3>
                    <p>Log in with a valid Gmail account and App Password, then click <strong>Refresh Inbox</strong>.</p>
                </div>
            </c:when>
            <c:otherwise>
                <div class="table-wrapper">
                    <table class="data-table">
                        <thead>
                        <tr>
                            <th>Sender</th>
                            <th>Subject</th>
                            <th>Received Time</th>
                            <th>Tags</th>
                            <th>Urgency Score</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach items="${emails}" var="email">
                            <tr>
                                <td data-label="Sender">
                                    <div class="sender-cell">
                                        <span class="sender-indicator"></span>
                                        <span>${email.sender}</span>
                                    </div>
                                </td>
                                <td data-label="Subject">
                                    <a class="subject-link" href="${pageContext.request.contextPath}/emails/${email.id}">${email.subject}</a>
                                </td>
                                <td data-label="Received Time">${email.receivedTimeDisplay}</td>
                                <td data-label="Tags">
                                    <div class="tag-list">
                                        <c:forEach items="${email.tagList}" var="tag">
                                            <span class="tag">${tag}</span>
                                        </c:forEach>
                                    </div>
                                </td>
                                <td data-label="Urgency Score">
                                    <span class="score-badge ${email.urgencyScore ge 70 ? 'high-score' : email.urgencyScore ge 40 ? 'mid-score' : 'low-score'}">
                                        ${email.urgencyScore}
                                    </span>
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div>
</body>
</html>
