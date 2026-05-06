<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Filter & Sort - Smart Email Analyzer</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css">
</head>
<body>
<div class="page-container">
    <%@ include file="common/header.jspf" %>

    <div class="page-toolbar">
        <div>
            <span class="section-kicker">Filter &amp; Sort</span>
            <h2>Refine your inbox results</h2>
            <p class="muted-text">Filter by tag and sort emails by urgency score or recency.</p>
        </div>
        <div class="toolbar-actions">
            <a class="btn btn-ghost" href="${pageContext.request.contextPath}/emails/inbox">Back to Inbox</a>
        </div>
    </div>

    <div class="panel">
        <form action="${pageContext.request.contextPath}/emails/filter" method="get" class="filter-form">
            <div class="form-field">
                <label for="tag">Filter by Tag</label>
                <select id="tag" name="tag">
                    <option value="">All Tags</option>
                    <c:forEach items="${availableTags}" var="tag">
                        <option value="${tag}" ${selectedTag eq tag ? 'selected' : ''}>${tag}</option>
                    </c:forEach>
                </select>
            </div>

            <div class="form-field">
                <label for="sort">Sort By</label>
                <select id="sort" name="sort">
                    <option value="scoreHigh" ${selectedSort eq 'scoreHigh' ? 'selected' : ''}>Urgency Score (High to Low)</option>
                    <option value="newest" ${selectedSort eq 'newest' ? 'selected' : ''}>Newest First</option>
                    <option value="scoreLow" ${selectedSort eq 'scoreLow' ? 'selected' : ''}>Urgency Score (Low to High)</option>
                </select>
            </div>

            <div class="filter-action-wrap">
                <button type="submit" class="btn btn-primary">Apply Filters</button>
                <a class="btn btn-outline" href="${pageContext.request.contextPath}/emails/filter">Reset</a>
            </div>
        </form>
    </div>

    <div class="panel table-panel">
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
                <c:if test="${empty emails}">
                    <tr>
                        <td colspan="5" class="empty-cell">No emails match the selected filter.</td>
                    </tr>
                </c:if>
                </tbody>
            </table>
        </div>
    </div>
</div>
</body>
</html>
