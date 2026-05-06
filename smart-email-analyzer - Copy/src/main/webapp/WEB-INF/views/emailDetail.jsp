<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Email Detail - Smart Email Analyzer</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css">
</head>
<body>
<div class="page-container">
    <%@ include file="common/header.jspf" %>

    <div class="page-toolbar">
        <div>
            <h2>Email Detail</h2>
            <p class="muted-text">Cleaned content, tags, and urgency score for the selected message.</p>
        </div>
        <a class="btn btn-secondary" href="${pageContext.request.contextPath}/emails/inbox">Back to Inbox</a>
    </div>

    <div class="detail-card">
        <h3>${emailMessage.subject}</h3>
        <div class="detail-meta">
            <div><strong>Sender:</strong> ${emailMessage.sender}</div>
            <div><strong>Received:</strong> ${emailMessage.receivedTimeDisplay}</div>
            <div><strong>Urgency Score:</strong> <span class="score-badge ${emailMessage.urgencyScore ge 70 ? 'high-score' : emailMessage.urgencyScore ge 40 ? 'mid-score' : 'low-score'}">${emailMessage.urgencyScore}</span></div>
        </div>

        <div class="tag-list detail-tags">
            <c:forEach items="${emailMessage.tagList}" var="tag">
                <span class="tag">${tag}</span>
            </c:forEach>
        </div>

        <div class="content-panel">
            <h4>Cleaned Email Content</h4>
            <pre class="email-content">${emailMessage.cleanedContent}</pre>
        </div>
    </div>

    <%@ include file="common/footer.jspf" %>
</div>
</body>
</html>
