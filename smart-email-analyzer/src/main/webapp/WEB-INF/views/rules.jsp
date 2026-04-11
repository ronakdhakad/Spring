<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Rule Management – Smart Email Analyzer</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css">
</head>
<body>

<jsp:include page="nav.jsp"/>

<div class="page-container">

    <div class="page-header">
        <h1> Rule Management</h1>
        <p>Define keywords and sender rules that drive urgency scoring and tagging.</p>
    </div>

    <!-- Flash messages -->
    <c:if test="${not empty success}"><div class="alert alert-success">${success}</div></c:if>
    <c:if test="${not empty error}">  <div class="alert alert-error">${error}</div></c:if>

    <div class="two-col-layout">

        <!-- ═══════════════════════════════════
             LEFT: Add / Edit Rule Form
        ════════════════════════════════════════ -->
        <div class="form-card">

            <c:choose>
                <c:when test="${not empty editRule}">
                    <h2> Edit Rule #${editRule.id}</h2>
                    <form action="${pageContext.request.contextPath}/rules/update/${editRule.id}"
                          method="post">
                </c:when>
                <c:otherwise>
                    <h2>Add New Rule</h2>
                    <form action="${pageContext.request.contextPath}/rules/add"
                          method="post">
                </c:otherwise>
            </c:choose>

                <!-- Rule Type -->
                <div class="form-group">
                    <label for="type">Rule Type *</label>
                    <select id="type" name="type" required onchange="toggleTypeHelp(this.value)">
                        <option value="KEYWORD" ${editRule.type == 'KEYWORD' ? 'selected' : ''}>KEYWORD</option>
                        <option value="SENDER"  ${editRule.type == 'SENDER'  ? 'selected' : ''}>SENDER</option>
                    </select>
                    <small id="typeHelp" class="form-hint">
                        KEYWORD: matches text in email subject or body.<br>
                        SENDER: matches the sender's email address.
                    </small>
                </div>

                <!-- Value -->
                <div class="form-group">
                    <label for="value">Value *</label>
                    <input type="text"
                           id="value"
                           name="value"
                           value="${not empty editRule ? editRule.value : ''}"
                           placeholder="e.g.  urgent  OR  boss@company.com"
                           required>
                    <small class="form-hint">Case-insensitive match against email content or sender.</small>
                </div>

                <!-- Tag -->
                <div class="form-group">
                    <label for="tag">Assign Tag</label>
                    <select id="tag" name="tag">
                        <option value="">— No tag —</option>
                        <option value="URGENT"    ${editRule.tag == 'URGENT'    ? 'selected' : ''}>URGENT</option>
                        <option value="PAYMENT"   ${editRule.tag == 'PAYMENT'   ? 'selected' : ''}>PAYMENT</option>
                        <option value="MEETING"   ${editRule.tag == 'MEETING'   ? 'selected' : ''}>MEETING</option>
                        <option value="PROMOTION" ${editRule.tag == 'PROMOTION' ? 'selected' : ''}>PROMOTION</option>
                        <option value="WORK"      ${editRule.tag == 'WORK'      ? 'selected' : ''}>WORK</option>
                    </select>
                </div>

                <!-- Score -->
                <div class="form-group">
                    <label for="score">Score Weight</label>
                    <input type="number"
                           id="score"
                           name="score"
                           value="${not empty editRule ? editRule.score : 10}"
                           min="-50" max="100"
                           placeholder="e.g. 30">
                    <small class="form-hint">Positive = more urgent. Negative = less urgent (spam signals).</small>
                </div>

                <div class="form-actions">
                    <c:choose>
                        <c:when test="${not empty editRule}">
                            <button type="submit" class="btn btn-primary"> Save Changes</button>
                            <a href="${pageContext.request.contextPath}/rules" class="btn btn-secondary">Cancel</a>
                        </c:when>
                        <c:otherwise>
                            <button type="submit" class="btn btn-primary"> Add Rule</button>
                        </c:otherwise>
                    </c:choose>
                </div>

            </form>

            <!-- Quick examples -->
            <div class="examples-box">
                <h4>Examples:</h4>
                <code>KEYWORD | urgent   → URGENT (+30)</code><br>
                <code>KEYWORD | payment  → PAYMENT (+25)</code><br>
                <code>SENDER  | boss@company.com → WORK (+20)</code><br>
                <code>KEYWORD | unsubscribe → PROMOTION (−5)</code>
            </div>
        </div>

        <!-- ═══════════════════════════════════
             RIGHT: Existing Rules Table
        ════════════════════════════════════════ -->
        <div class="table-card">
            <h2>Current Rules (${rules.size()})</h2>

            <c:choose>
                <c:when test="${empty rules}">
                    <div class="empty-state">No rules defined yet. Add one using the form.</div>
                </c:when>
                <c:otherwise>
                    <table class="rules-table">
                        <thead>
                            <tr>
                                <th>#</th>
                                <th>Type</th>
                                <th>Value</th>
                                <th>Tag</th>
                                <th>Score</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                        <c:forEach var="rule" items="${rules}">
                            <tr>
                                <td>${rule.id}</td>
                                <td>
                                    <span class="type-badge type-${rule.type.toLowerCase()}">${rule.type}</span>
                                </td>
                                <td><code>${rule.value}</code></td>
                                <td>
                                    <c:if test="${not empty rule.tag}">
                                        <span class="tag tag-${rule.tag.toLowerCase()}">${rule.tag}</span>
                                    </c:if>
                                    <c:if test="${empty rule.tag}">—</c:if>
                                </td>
                                <td>
                                    <span class="${rule.score >= 0 ? 'score-positive' : 'score-negative'}">
                                        ${rule.score >= 0 ? '+' : ''}${rule.score}
                                    </span>
                                </td>
                                <td class="actions-cell">
                                    <a href="${pageContext.request.contextPath}/rules/edit/${rule.id}"
                                       class="btn btn-small">✏️ Edit</a>
                                    <a href="${pageContext.request.contextPath}/rules/delete/${rule.id}"
                                       class="btn btn-small btn-danger"
                                       onclick="return confirm('Delete rule: ${rule.value}?')">🗑️ Delete</a>
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </c:otherwise>
            </c:choose>
        </div>

    </div><!-- /two-col-layout -->

</div>

<script>
function toggleTypeHelp(type) {
    var hint = document.getElementById('typeHelp');
    if (type === 'KEYWORD') {
        hint.innerHTML = 'KEYWORD: matches text in email subject or body.';
    } else {
        hint.innerHTML = 'SENDER: matches the sender\'s email address (partial match).';
    }
}
</script>

</body>
</html>
