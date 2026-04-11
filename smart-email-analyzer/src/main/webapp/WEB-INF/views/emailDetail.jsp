<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c"   uri="jakarta.tags.core" %>
<%@ taglib prefix="fn"  uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>${email.subject} – Smart Email Analyzer</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css">
</head>
<body>

<jsp:include page="nav.jsp"/>

<div class="page-container">

    <!-- Back link -->
    <a href="${pageContext.request.contextPath}/inbox" class="back-link">← Back to Inbox</a>

    <!-- Flash messages -->
    <c:if test="${not empty success}"><div class="alert alert-success">${success}</div></c:if>
    <c:if test="${not empty error}">  <div class="alert alert-error">${error}</div></c:if>

    <!-- ═══════════════════════════════════
         Email Header Card
    ════════════════════════════════════════ -->
    <div class="detail-card">

        <div class="detail-header">
            <h1 class="email-subject">${email.subject}</h1>

            <div class="email-meta">
                <div class="meta-row">
                    <span class="meta-label">From:</span>
                    <span class="meta-value">${email.sender}</span>
                </div>
                <div class="meta-row">
                    <span class="meta-label">Received:</span>
                    <span class="meta-value">${email.receivedTime}</span>
                </div>
                <div class="meta-row">
                    <span class="meta-label">Tags:</span>
                    <span class="meta-value">
                        <c:forEach var="tag" items="${email.tags}">
                            <span class="tag tag-${fn:toLowerCase(tag.name)}">${tag.name}</span>
                        </c:forEach>
                        <c:if test="${empty email.tags}">
                            <span class="tag tag-none">NONE</span>
                        </c:if>
                    </span>
                </div>
            </div>
        </div>

        <!-- ═══════════════════════════════════
             Urgency Score Panel
        ════════════════════════════════════════ -->
        <div class="score-panel">
            <div class="score-panel-left">
                <div class="big-score ${email.scoreValue >= 70 ? 'score-high' : email.scoreValue >= 40 ? 'score-medium' : 'score-low'}">
                    ${email.scoreValue}
                </div>
                <p class="score-label">Urgency Score</p>
                <c:if test="${email.urgencyScore.manualOverride}">
                    <span class="badge-override">✏️ Manually Set</span>
                </c:if>
            </div>

            <div class="score-panel-right">
                <h3>Score Breakdown</h3>
                <pre class="breakdown-text"><c:out value="${email.urgencyScore.breakdown}" default="No breakdown available."/></pre>
            </div>
        </div>

        <!-- ═══════════════════════════════════
             Manual Score Override
        ════════════════════════════════════════ -->
        <div class="section-card">
            <h3> Override Urgency Score</h3>
            <form action="${pageContext.request.contextPath}/email/${email.id}/updateScore"
                  method="post" class="inline-form">
                <label for="score">New Score (0 – 100):</label>
                <input type="number"
                       id="score"
                       name="score"
                       min="0" max="100"
                       value="${email.scoreValue}"
                       required>
                <button type="submit" class="btn btn-primary">Update Score</button>
            </form>
        </div>

        <!-- ═══════════════════════════════════
             Email Body
        ════════════════════════════════════════ -->
        <div class="section-card">
            <h3>Email Content</h3>
            <div class="email-body">
                <c:out value="${email.body}" default="(no content)"/>
            </div>
        </div>

        <!-- ═══════════════════════════════════
             Reply Form
        ════════════════════════════════════════ -->
        <div class="section-card">
            <h3> Reply to ${email.sender}</h3>
            <form action="${pageContext.request.contextPath}/email/${email.id}/reply"
                  method="post">
                <div class="form-group">
                    <label for="replyBody">Your Reply:</label>
                    <textarea id="replyBody"
                              name="replyBody"
                              rows="6"
                              placeholder="Type your reply here..."
                              required></textarea>
                </div>
                <button type="submit" class="btn btn-primary">Send Reply</button>
            </form>
        </div>

    </div><!-- /detail-card -->

</div><!-- /page-container -->

</body>
</html>
