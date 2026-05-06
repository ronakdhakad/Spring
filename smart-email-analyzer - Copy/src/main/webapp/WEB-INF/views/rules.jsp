<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Rule Management - Smart Email Analyzer</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css">
</head>
<body>
<div class="page-container">
    <%@ include file="common/header.jspf" %>

    <div class="page-toolbar">
        <div>
            <h2>Rule Management</h2>
            <p class="muted-text">Create custom keyword mappings, set sender importance, and change score weights.</p>
        </div>
    </div>

    <div class="rules-grid">
        <div class="panel">
            <h3>Add Keyword Rule</h3>
            <form action="${pageContext.request.contextPath}/rules/keyword" method="post" class="form-grid compact-form">
                <label>Keyword</label>
                <input type="text" name="keyword" placeholder="urgent" required>

                <label>Tag</label>
                <input type="text" name="tag" placeholder="URGENT" required>

                <label>Score Boost</label>
                <input type="number" name="scoreBoost" value="30">

                <button type="submit" class="btn btn-primary">Add Keyword Rule</button>
            </form>

            <h4 class="section-space">Existing Keyword Rules</h4>
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
                        <td>${rule.keyword}</td>
                        <td><span class="tag">${rule.tag}</span></td>
                        <td>${rule.scoreBoost}</td>
                        <td>
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

        <div class="panel">
            <h3>Add / Update Sender Rule</h3>
            <form action="${pageContext.request.contextPath}/rules/sender" method="post" class="form-grid compact-form">
                <label>Sender Email</label>
                <input type="email" name="senderEmail" placeholder="boss@company.com" required>

                <label>Score Boost</label>
                <input type="number" name="scoreBoost" value="20">

                <button type="submit" class="btn btn-primary">Save Sender Rule</button>
            </form>

            <h4 class="section-space">Existing Sender Rules</h4>
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
                        <td>${rule.senderEmail}</td>
                        <td>${rule.scoreBoost}</td>
                        <td>
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

    <div class="panel section-space">
        <h3>Adjust Score Weights</h3>
        <form action="${pageContext.request.contextPath}/rules/weights" method="post">
            <table class="data-table">
                <thead>
                <tr>
                    <th>Weight Key</th>
                    <th>Current Value</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach items="${scoreWeights}" var="weight">
                    <tr>
                        <td>${weight.weightKey}</td>
                        <td>
                            <input type="number" name="${weight.weightKey}" value="${weight.weightValue}" class="weight-input">
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
            <div class="weight-action-row">
                <button type="submit" class="btn btn-primary">Update Weights</button>
            </div>
        </form>
    </div>

    <%@ include file="common/footer.jspf" %>
</div>
</body>
</html>
