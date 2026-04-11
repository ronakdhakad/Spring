<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c"   uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"  %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Inbox – Smart Email Analyzer</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css">
</head>
<body>

<!-- ═══════════════════════════════════════════════════════════
     Navigation
════════════════════════════════════════════════════════════════ -->
<jsp:include page="nav.jsp"/>

<div class="page-container">

    <div class="page-header">
        <h1> Inbox</h1>
        <p>Logged in as: <strong>${user.email}</strong></p>
    </div>

    <!-- Flash messages -->
    <c:if test="${not empty success}"><div class="alert alert-success">${success}</div></c:if>
    <c:if test="${not empty error}">  <div class="alert alert-error">${error}</div></c:if>

    <!-- Quick links -->
    <div class="action-bar">
        <a href="${pageContext.request.contextPath}/filter?sort=score" class="btn btn-secondary">
             Sort by Score
        </a>
        <a href="${pageContext.request.contextPath}/filter" class="btn btn-secondary">
             Filter & Search
        </a>
        <a href="${pageContext.request.contextPath}/dashboard" class="btn btn-secondary">
             Dashboard
        </a>
    </div>

    <!-- Email table -->
    <c:choose>
        <c:when test="${empty emails}">
            <div class="empty-state">
                <p> No emails found. The scheduler will fetch your emails shortly.</p>
            </div>
        </c:when>
        <c:otherwise>
            <table class="email-table">
                <thead>
                    <tr>
                        <th>Score</th>
                        <th>Sender</th>
                        <th>Subject</th>
                        <th>Tags</th>
                        <th>Received</th>
                        <th>Action</th>
                    </tr>
                </thead>
                <tbody>
                <c:forEach var="email" items="${emails}">
                    <tr class="email-row ${email.scoreValue >= 70 ? 'row-urgent' : email.scoreValue >= 40 ? 'row-medium' : 'row-low'}">

                        <!-- Urgency Score Badge -->
                        <td>
                            <span class="score-badge ${email.scoreValue >= 70 ? 'score-high' : email.scoreValue >= 40 ? 'score-medium' : 'score-low'}">
                                ${email.scoreValue}
                            </span>
                        </td>

                        <!-- Sender -->
                        <td class="sender-cell">
                            <span title="${email.sender}">${email.sender}</span>
                        </td>

                        <!-- Subject -->
                        <td class="subject-cell">
                            <a href="${pageContext.request.contextPath}/email/${email.id}">
                                ${email.subject}
                            </a>
                        </td>

                        <!-- Tags -->
                        <td>
                            <c:forEach var="tag" items="${email.tags}">
                                <span class="tag tag-${tag.name.toLowerCase()}">${tag.name}</span>
                            </c:forEach>
                        </td>

                        <!-- Received Time -->
                        <td>
                            <fmt:formatDate value="${email.receivedTime}" pattern="dd MMM HH:mm" type="both"/>
                            <c:if test="${empty email.receivedTime}">—</c:if>
                        </td>

                        <!-- View Action -->
                        <td>
                            <a href="${pageContext.request.contextPath}/email/${email.id}"
                               class="btn btn-small">View</a>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>

            <div class="table-footer">
                Total: <strong>${emails.size()}</strong> email(s)
            </div>
        </c:otherwise>
    </c:choose>

</div>

</body>
</html>
