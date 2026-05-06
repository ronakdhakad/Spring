<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Rule Management - Smart Email Analyzer</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css">
</head>
<body>
<div class="page-container">
    <%@ include file="common/header.jspf" %>

    <div class="page-toolbar">
        <div>
            <span class="section-kicker">Rules</span>
            <h2>Manage scoring rules</h2>
            <p class="muted-text">Create custom keyword mappings and sender importance rules to improve urgency analysis.</p>
        </div>
    </div>

    <div class="panel page-banner">
        <div>
            <h3>How rules work</h3>
            <p class="muted-text">Keyword rules add tags and score boosts when matching words appear. Sender rules increase importance for trusted or high-priority email addresses.</p>
        </div>
        <div class="feature-list compact-pills">
            <span class="feature-pill light-pill">Keyword → Tag</span>
            <span class="feature-pill light-pill">Sender Boost</span>
            <span class="feature-pill light-pill">Custom Scoring</span>
        </div>
    </div>

    <div class="rules-grid">
        <div class="panel">
            <div class="panel-head">
                <div>
                    <h3>Add Keyword Rule</h3>
                    <p class="muted-text">Example: <strong>urgent</strong> → <strong>URGENT</strong> (+30)</p>
                </div>
            </div>

            <form action="${pageContext.request.contextPath}/rules/keyword" method="post" class="compact-form">
                <div class="form-field">
                    <label for="keyword">Keyword</label>
                    <input id="keyword" type="text" name="keyword" placeholder="urgent" required>
                </div>

                <div class="form-field">
                    <label for="tag">Tag</label>
                    <input id="tag" type="text" name="tag" placeholder="URGENT" required>
                </div>

                <div class="form-field">
                    <label for="scoreBoost">Score Boost</label>
                    <input id="scoreBoost" type="number" name="scoreBoost" value="30">
                </div>

                <div class="form-actions full-width">
                    <button type="submit" class="btn btn-primary">Add Keyword Rule</button>
                </div>
            </form>

            <h4 class="section-space">Existing Keyword Rules</h4>
            <div class="table-wrapper compact-table">
                <table class="data-table">
                    <thead>
                    <tr>
                        <th>Keyword</th>
                        <th>Tag</th>
                        <th>Boost</th>
                        <th>Action</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach items="${keywordRules}" var="rule">
                        <tr>
                            <td data-label="Keyword">${rule.keyword}</td>
                            <td data-label="Tag"><span class="tag">${rule.tag}</span></td>
                            <td data-label="Boost">${rule.scoreBoost}</td>
                            <td data-label="Action">
                                <form action="${pageContext.request.contextPath}/rules/keyword/${rule.id}/delete" method="post">
                                    <button type="submit" class="btn btn-danger btn-small">Delete</button>
                                </form>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty keywordRules}">
                        <tr>
                            <td colspan="4" class="empty-cell">No keyword rules added yet.</td>
                        </tr>
                    </c:if>
                    </tbody>
                </table>
            </div>
        </div>

        <div class="panel">
            <div class="panel-head">
                <div>
                    <h3>Add / Update Sender Rule</h3>
                    <p class="muted-text">Example: <strong>boss@company.com</strong> gets a positive urgency boost.</p>
                </div>
            </div>

            <form action="${pageContext.request.contextPath}/rules/sender" method="post" class="compact-form">
                <div class="form-field">
                    <label for="senderEmail">Sender Email</label>
                    <input id="senderEmail" type="email" name="senderEmail" placeholder="boss@company.com" required>
                </div>

                <div class="form-field">
                    <label for="senderScoreBoost">Score Boost</label>
                    <input id="senderScoreBoost" type="number" name="scoreBoost" value="20">
                </div>

                <div class="form-actions full-width">
                    <button type="submit" class="btn btn-primary">Save Sender Rule</button>
                </div>
            </form>

            <h4 class="section-space">Existing Sender Rules</h4>
            <div class="table-wrapper compact-table">
                <table class="data-table">
                    <thead>
                    <tr>
                        <th>Sender</th>
                        <th>Boost</th>
                        <th>Action</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach items="${senderRules}" var="rule">
                        <tr>
                            <td data-label="Sender">${rule.senderEmail}</td>
                            <td data-label="Boost">${rule.scoreBoost}</td>
                            <td data-label="Action">
                                <form action="${pageContext.request.contextPath}/rules/sender/${rule.id}/delete" method="post">
                                    <button type="submit" class="btn btn-danger btn-small">Delete</button>
                                </form>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty senderRules}">
                        <tr>
                            <td colspan="3" class="empty-cell">No sender rules added yet.</td>
                        </tr>
                    </c:if>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>
</body>
</html>
