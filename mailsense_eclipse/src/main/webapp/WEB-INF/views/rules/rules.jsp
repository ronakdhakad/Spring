<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8"/><meta name="viewport" content="width=device-width, initial-scale=1"/>
  <title>Tag Rules — MailSense</title>
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"/>
  <style>body{background:#f8fafc}</style>
</head>
<body>
<%@ include file="../_navbar.jsp" %>
<div class="container py-4" style="max-width:900px">
  <h4 class="mb-1">Tag Rules</h4>
  <p class="text-muted mb-4">Automatically tag incoming emails based on rules you define.</p>

  <c:if test="${not empty ruleSuccess}">
    <div class="alert alert-success alert-dismissible fade show">${ruleSuccess}
      <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
  </c:if>

  <div class="row g-4">
    <div class="col-md-4">
      <div class="card border-0 shadow-sm" style="border-radius:12px">
        <div class="card-header bg-white border-bottom fw-semibold">+ New Rule</div>
        <div class="card-body p-3">
          <form action="${pageContext.request.contextPath}/rules" method="post">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
            <div class="mb-2">
              <label class="form-label small fw-semibold">If field</label>
              <select name="fieldToMatch" class="form-select form-select-sm">
                <c:forEach var="f" items="${fields}"><option>${f}</option></c:forEach>
              </select>
            </div>
            <div class="mb-2">
              <label class="form-label small fw-semibold">Operator</label>
              <select name="operator" class="form-select form-select-sm">
                <c:forEach var="op" items="${operators}"><option>${op}</option></c:forEach>
              </select>
            </div>
            <div class="mb-2">
              <label class="form-label small fw-semibold">Keyword</label>
              <input type="text" name="keyword" class="form-control form-control-sm"
                     placeholder="e.g. invoice" required/>
            </div>
            <div class="mb-2">
              <label class="form-label small fw-semibold">Apply tag</label>
              <input type="text" name="tagToApply" class="form-control form-control-sm"
                     placeholder="e.g. FINANCE" required/>
            </div>
            <div class="mb-3">
              <label class="form-label small fw-semibold">Priority (lower = first)</label>
              <input type="number" name="priority" class="form-control form-control-sm"
                     value="1" min="1"/>
            </div>
            <button type="submit" class="btn btn-primary btn-sm w-100">Add Rule</button>
          </form>
        </div>
      </div>
    </div>

    <div class="col-md-8">
      <div class="card border-0 shadow-sm" style="border-radius:12px">
        <div class="card-header bg-white border-bottom fw-semibold">
          Your Rules (${rules.size()})
        </div>
        <div class="table-responsive">
          <table class="table table-hover align-middle mb-0">
            <thead class="table-light">
              <tr><th>#</th><th>Field</th><th>Op</th><th>Keyword</th><th>Tag</th><th></th></tr>
            </thead>
            <tbody>
              <c:choose>
                <c:when test="${empty rules}">
                  <tr><td colspan="6" class="text-center py-4 text-muted">No rules yet.</td></tr>
                </c:when>
                <c:otherwise>
                  <c:forEach var="r" items="${rules}">
                    <tr>
                      <td>${r.priority}</td>
                      <td>${r.fieldToMatch}</td>
                      <td><span class="badge bg-light text-dark">${r.operator}</span></td>
                      <td><code>${r.keyword}</code></td>
                      <td><span class="badge rounded-pill"
                                style="background:#dbeafe;color:#1d4ed8">${r.tagToApply}</span></td>
                      <td>
                        <a href="${pageContext.request.contextPath}/rules/${r.id}/edit"
                           class="btn btn-sm btn-outline-secondary py-0">Edit</a>
                        <form action="${pageContext.request.contextPath}/rules/${r.id}/delete"
                              method="post" class="d-inline"
                              onsubmit="return confirm('Delete?')">
                          <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                          <button class="btn btn-sm btn-outline-danger py-0">Del</button>
                        </form>
                      </td>
                    </tr>
                  </c:forEach>
                </c:otherwise>
              </c:choose>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </div>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
