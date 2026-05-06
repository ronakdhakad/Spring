<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Email Detail - Smart Email Analyzer</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css">
</head>
<body>
<div class="page-container">
    <%@ include file="common/header.jspf" %>

    <div class="page-toolbar">
        <div>
            <span class="section-kicker">Email Detail</span>
            <h2>Message analysis view</h2>
            <p class="muted-text">Review cleaned content, tags, sender details, and urgency score in one place.</p>
        </div>
        <a class="btn btn-outline" href="${pageContext.request.contextPath}/emails/inbox">Back to Inbox</a>
    </div>

    <div class="detail-card">
        <div class="detail-header">
            <h3>${emailMessage.subject}</h3>
            <span class="score-badge ${emailMessage.urgencyScore ge 70 ? 'high-score' : emailMessage.urgencyScore ge 40 ? 'mid-score' : 'low-score'}">Score ${emailMessage.urgencyScore}</span>
        </div>

        <div class="detail-meta">
            <div class="meta-box">
                <span class="meta-label">Sender</span>
                <div class="meta-value">${emailMessage.sender}</div>
            </div>
            <div class="meta-box">
                <span class="meta-label">Received Time</span>
                <div class="meta-value">${emailMessage.receivedTimeDisplay}</div>
            </div>
            <div class="meta-box">
                <span class="meta-label">Tags</span>
                <div class="tag-list detail-tags">
                    <c:choose>
                        <c:when test="${not empty emailMessage.tagList}">
                            <c:forEach items="${emailMessage.tagList}" var="tag">
                                <span class="tag">${tag}</span>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <span class="tag">NO TAGS</span>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>

        <div class="content-panel">
            <div class="content-panel-head">
                <h4>Cleaned Email Content</h4>
                <span class="content-panel-note">Readable version of the selected email</span>
            </div>
            <pre class="email-content">${emailMessage.cleanedContent}</pre>
        </div>
    </div>
</div>
</body>
</html>
