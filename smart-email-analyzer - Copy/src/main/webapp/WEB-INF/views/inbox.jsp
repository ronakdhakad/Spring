<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Inbox - Smart Email Analyzer</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css">
</head>
<body>
<div class="page-container">
    <%@ include file="common/header.jspf" %>

    <div class="page-toolbar">
        <div>
            <h2>Inbox</h2>
            <p class="muted-text">Only emails belonging to the logged-in user are shown here.</p>
        </div>
        <div class="toolbar-actions">
            <a class="btn btn-secondary" href="${pageContext.request.contextPath}/emails/refresh">Refresh Inbox</a>
            <a class="btn btn-secondary" href="${pageContext.request.contextPath}/emails/filter">Filter &amp; Sort</a>
        </div>
    </div>

    <div class="panel">
        <c:choose>
            <c:when test="${empty emails}">
                <div class="empty-state">
                    <h3>No emails found</h3>
                    <p>Log in with a valid Gmail account and App Password, then click “Refresh Inbox”.</p>
                </div>
            </c:when>
            <c:otherwise>
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
                            <td>${email.sender}</td>
                            <td>
                                <a class="subject-link" href="${pageContext.request.contextPath}/emails/${email.id}">${email.subject}</a>
                            </td>
                            <td>${email.receivedTimeDisplay}</td>
                            <td>
                                <div class="tag-list">
                                    <c:forEach items="${email.tagList}" var="tag">
                                        <span class="tag">${tag}</span>
                                    </c:forEach>
                                </div>
                            </td>
                            <td>
                                <span class="score-badge ${email.urgencyScore ge 70 ? 'high-score' : email.urgencyScore ge 40 ? 'mid-score' : 'low-score'}">
                                    ${email.urgencyScore}
                                </span>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </c:otherwise>
        </c:choose>
    </div>

    <%@ include file="common/footer.jspf" %>
</div>
</body>
</html>
