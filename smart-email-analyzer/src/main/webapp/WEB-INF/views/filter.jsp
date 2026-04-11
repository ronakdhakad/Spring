<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c"   uri="jakarta.tags.core" %>
<%@ taglib prefix="fn"  uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Filter & Sort – Smart Email Analyzer</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css">
</head>
<body>

<jsp:include page="nav.jsp"/>

<div class="page-container">

    <div class="page-header">
        <h1> Filter &amp; Sort Emails</h1>
    </div>

    <!-- ═══════════════════════════════════
         Filter Controls
    ════════════════════════════════════════ -->
    <div class="filter-controls">

        <!-- Search by subject -->
        <form action="${pageContext.request.contextPath}/filter" method="get" class="search-form">
            <input type="text"
                   name="q"
                   placeholder="Search by subject..."
                   value="${query}">
            <button type="submit" class="btn btn-primary">Search</button>
        </form>

        <!-- Filter by tag buttons -->
        <div class="tag-filter-bar">
            <span class="filter-label">Filter by tag:</span>
            <a href="${pageContext.request.contextPath}/filter"
               class="btn btn-tag ${empty activeTag ? 'active' : ''}">All</a>
            <c:forEach var="tag" items="${allTags}">
                <a href="${pageContext.request.contextPath}/filter?tag=${tag.name}"
                   class="btn btn-tag tag-${fn:toLowerCase(tag.name)} ${activeTag == tag.name ? 'active' : ''}">
                    ${tag.name}
                </a>
            </c:forEach>
        </div>

        <!-- Sort by score -->
        <div class="sort-bar">
            <span class="filter-label">Sort:</span>
            <a href="${pageContext.request.contextPath}/filter?sort=score"
               class="btn btn-secondary ${sortBy == 'score' ? 'active' : ''}">
                By Urgency Score (High → Low)
            </a>
            <a href="${pageContext.request.contextPath}/filter"
               class="btn btn-secondary ${empty sortBy ? 'active' : ''}">
                By Date (Newest First)
            </a>
        </div>

    </div><!-- /filter-controls -->

    <!-- ═══════════════════════════════════
         Active filter summary
    ════════════════════════════════════════ -->
    <c:if test="${not empty activeTag or not empty sortBy or not empty query}">
        <div class="filter-summary">
            Showing:
            <c:if test="${not empty activeTag}"> tag = <strong>${activeTag}</strong></c:if>
            <c:if test="${not empty sortBy}">    sorted by <strong>${sortBy}</strong></c:if>
            <c:if test="${not empty query}">     matching "<strong>${query}</strong>"</c:if>
            — <a href="${pageContext.request.contextPath}/filter">Clear filters</a>
        </div>
    </c:if>

    <!-- ═══════════════════════════════════
         Results Table
    ════════════════════════════════════════ -->
    <c:choose>
        <c:when test="${empty emails}">
            <div class="empty-state"> No emails match your filter criteria.</div>
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
                        <th></th>
                    </tr>
                </thead>
                <tbody>
                <c:forEach var="email" items="${emails}">
                    <tr class="email-row ${email.scoreValue >= 70 ? 'row-urgent' : email.scoreValue >= 40 ? 'row-medium' : 'row-low'}">

                        <td>
                            <span class="score-badge ${email.scoreValue >= 70 ? 'score-high' : email.scoreValue >= 40 ? 'score-medium' : 'score-low'}">
                                ${email.scoreValue}
                            </span>
                        </td>

                        <td class="sender-cell">${email.sender}</td>

                        <td class="subject-cell">
                            <a href="${pageContext.request.contextPath}/email/${email.id}">
                                ${email.subject}
                            </a>
                        </td>

                        <td>
                            <c:forEach var="tag" items="${email.tags}">
                                <span class="tag tag-${fn:toLowerCase(tag.name)}">${tag.name}</span>
                            </c:forEach>
                        </td>

                        <td>${email.receivedTime}</td>

                        <td>
                            <a href="${pageContext.request.contextPath}/email/${email.id}"
                               class="btn btn-small">View</a>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>

            <div class="table-footer">
                <strong>${emails.size()}</strong> result(s) found.
            </div>
        </c:otherwise>
    </c:choose>

</div>

</body>
</html>
