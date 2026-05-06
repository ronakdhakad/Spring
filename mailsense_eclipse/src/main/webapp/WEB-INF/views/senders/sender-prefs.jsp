<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8"/><meta name="viewport" content="width=device-width, initial-scale=1"/>
  <title>Sender Preferences — MailSense</title>
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"/>
  <style>body{background:#f8fafc}</style>
</head>
<body>
<%@ include file="../_navbar.jsp" %>
<div class="container py-4" style="max-width:800px">
  <h4 class="mb-1">Sender Preferences</h4>
  <p class="text-muted mb-4">
    Whitelist (+20 score) or Blacklist (-20 score) specific senders.
  </p>

  <c:if test="${not empty prefSuccess}">
    <div class="alert alert-success alert-dismissible fade show">${prefSuccess}
      <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
  </c:if>
  <c:if test="${not empty prefError}">
    <div class="alert alert-danger alert-dismissible fade show">${prefError}
      <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
  </c:if>

  <div class="row g-4">
    <div class="col-md-4">
      <div class="card border-0 shadow-sm" style="border-radius:12px">
        <div class="card-header bg-white border-bottom fw-semibold">Add Preference</div>
        <div class="card-body p-3">
          <form action="${pageContext.request.contextPath}/senders" method="post">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
            <div class="mb-2">
              <label class="form-label small fw-semibold">Sender Email</label>
              <input type="email" name="senderEmail" class="form-control form-control-sm"
                     placeholder="boss@company.com" required/>
            </div>
            <div class="mb-3">
              <label class="form-label small fw-semibold">Type</label>
              <select name="prefType" class="form-select form-select-sm">
                <option value="WHITELIST">Whitelist (+20)</option>
                <option value="BLACKLIST">Blacklist (-20)</option>
              </select>
            </div>
            <button type="submit" class="btn btn-primary btn-sm w-100">Add</button>
          </form>
        </div>
      </div>
    </div>

    <div class="col-md-8">
      <div class="card border-0 shadow-sm" style="border-radius:12px">
        <div class="table-responsive">
          <table class="table table-hover align-middle mb-0">
            <thead class="table-light">
              <tr><th>Email</th><th>Type</th><th>Modifier</th><th></th></tr>
            </thead>
            <tbody>
              <c:choose>
                <c:when test="${empty prefs}">
                  <tr><td colspan="4" class="text-center py-4 text-muted">No preferences yet.</td></tr>
                </c:when>
                <c:otherwise>
                  <c:forEach var="p" items="${prefs}">
                    <tr>
                      <td>${p.senderEmail}</td>
                      <td>
                        <span class="badge ${p.prefType == 'WHITELIST' ? 'bg-success' : 'bg-danger'}">
                          ${p.prefType}
                        </span>
                      </td>
                      <td class="${p.scoreModifier > 0 ? 'text-success' : 'text-danger'} fw-bold">
                        ${p.scoreModifier > 0 ? '+' : ''}${p.scoreModifier}
                      </td>
                      <td>
                        <form action="${pageContext.request.contextPath}/senders/${p.id}/delete"
                              method="post" class="d-inline"
                              onsubmit="return confirm('Remove?')">
                          <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                          <button class="btn btn-sm btn-outline-danger py-0">Remove</button>
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
