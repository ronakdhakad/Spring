<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8"/><meta name="viewport" content="width=device-width, initial-scale=1"/>
  <title>Edit Rule — MailSense</title>
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"/>
  <style>body{background:#f8fafc}</style>
</head>
<body>
<%@ include file="../_navbar.jsp" %>
<div class="container py-4" style="max-width:500px">
  <h4 class="mb-4">Edit Rule</h4>
  <div class="card border-0 shadow-sm p-4" style="border-radius:12px">
    <form action="${pageContext.request.contextPath}/rules/${rule.id}/update" method="post">
      <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
      <div class="mb-3">
        <label class="form-label fw-semibold">If field</label>
        <select name="fieldToMatch" class="form-select">
          <c:forEach var="f" items="${fields}">
            <option ${rule.fieldToMatch == f ? 'selected' : ''}>${f}</option>
          </c:forEach>
        </select>
      </div>
      <div class="mb-3">
        <label class="form-label fw-semibold">Operator</label>
        <select name="operator" class="form-select">
          <c:forEach var="op" items="${operators}">
            <option ${rule.operator == op ? 'selected' : ''}>${op}</option>
          </c:forEach>
        </select>
      </div>
      <div class="mb-3">
        <label class="form-label fw-semibold">Keyword</label>
        <input type="text" name="keyword" class="form-control" value="${rule.keyword}" required/>
      </div>
      <div class="mb-3">
        <label class="form-label fw-semibold">Apply tag</label>
        <input type="text" name="tagToApply" class="form-control" value="${rule.tagToApply}" required/>
      </div>
      <div class="mb-4">
        <label class="form-label fw-semibold">Priority</label>
        <input type="number" name="priority" class="form-control" value="${rule.priority}" min="1"/>
      </div>
      <div class="d-flex gap-2">
        <button type="submit" class="btn btn-primary w-100">Save Changes</button>
        <a href="${pageContext.request.contextPath}/rules"
           class="btn btn-outline-secondary w-100">Cancel</a>
      </div>
    </form>
  </div>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
