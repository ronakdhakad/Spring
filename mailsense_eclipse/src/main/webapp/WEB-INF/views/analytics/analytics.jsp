<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8"/><meta name="viewport" content="width=device-width, initial-scale=1"/>
  <title>Analytics — MailSense</title>
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"/>
  <style>
    body{background:#f8fafc}
    .stat-card{border-radius:12px;border:none;transition:transform .15s}
    .stat-card:hover{transform:translateY(-2px)}
    .bar{height:22px;border-radius:4px;transition:width .4s}
  </style>
</head>
<body>
<%@ include file="../_navbar.jsp" %>
<div class="container py-4" style="max-width:960px">
  <h4 class="mb-1">Analytics</h4>
  <p class="text-muted mb-4">Inbox intelligence overview.</p>

  <c:set var="total" value="${criticalCount+highCount+mediumCount+lowCount}"/>

  <div class="row g-3 mb-4">
    <div class="col-6 col-md-3">
      <div class="card stat-card shadow-sm p-3 text-center">
        <div class="fs-2 fw-bold text-secondary">${total}</div>
        <div class="text-muted small">Total</div>
      </div>
    </div>
    <div class="col-6 col-md-3">
      <div class="card stat-card shadow-sm p-3 text-center">
        <div class="fs-2 fw-bold text-danger">${criticalCount}</div>
        <div class="text-muted small">Critical</div>
      </div>
    </div>
    <div class="col-6 col-md-3">
      <div class="card stat-card shadow-sm p-3 text-center">
        <div class="fs-2 fw-bold text-warning">${highCount}</div>
        <div class="text-muted small">High</div>
      </div>
    </div>
    <div class="col-6 col-md-3">
      <div class="card stat-card shadow-sm p-3 text-center">
        <div class="fs-2 fw-bold text-primary">${unreadCount}</div>
        <div class="text-muted small">Unread</div>
      </div>
    </div>
  </div>

  <div class="row g-4">
    <div class="col-md-6">
      <div class="card border-0 shadow-sm p-4" style="border-radius:12px">
        <h6 class="fw-semibold mb-3">Urgency Distribution</h6>
        <c:if test="${total > 0}">
          <div class="mb-3">
            <div class="d-flex justify-content-between small mb-1">
              <span class="text-danger fw-semibold">CRITICAL</span><span>${criticalCount}</span>
            </div>
            <div class="bar bg-danger bg-opacity-50" style="width:${criticalCount*100/total}%"></div>
          </div>
          <div class="mb-3">
            <div class="d-flex justify-content-between small mb-1">
              <span class="text-warning fw-semibold">HIGH</span><span>${highCount}</span>
            </div>
            <div class="bar bg-warning" style="width:${highCount*100/total}%"></div>
          </div>
          <div class="mb-3">
            <div class="d-flex justify-content-between small mb-1">
              <span class="fw-semibold" style="color:#ca8a04">MEDIUM</span><span>${mediumCount}</span>
            </div>
            <div class="bar bg-info" style="width:${mediumCount*100/total}%"></div>
          </div>
          <div class="mb-1">
            <div class="d-flex justify-content-between small mb-1">
              <span class="text-success fw-semibold">LOW</span><span>${lowCount}</span>
            </div>
            <div class="bar bg-success" style="width:${lowCount*100/total}%"></div>
          </div>
        </c:if>
        <c:if test="${total == 0}">
          <p class="text-muted text-center py-3">No emails yet.</p>
        </c:if>
      </div>
    </div>

    <div class="col-md-6">
      <div class="card border-0 shadow-sm p-4" style="border-radius:12px">
        <h6 class="fw-semibold mb-3">Top Senders</h6>
        <c:choose>
          <c:when test="${empty topSenders}">
            <p class="text-muted text-center py-3">No data yet.</p>
          </c:when>
          <c:otherwise>
            <table class="table table-sm table-hover mb-0">
              <thead class="table-light">
                <tr><th>#</th><th>Sender</th><th>Count</th></tr>
              </thead>
              <tbody>
                <c:forEach var="row" items="${topSenders}" varStatus="s">
                  <tr>
                    <td class="text-muted">${s.index+1}</td>
                    <td class="text-truncate" style="max-width:200px" title="${row[0]}">${row[0]}</td>
                    <td><span class="badge bg-primary">${row[1]}</span></td>
                  </tr>
                </c:forEach>
              </tbody>
            </table>
          </c:otherwise>
        </c:choose>
      </div>
    </div>
  </div>

  <div class="mt-4 d-flex gap-2">
    <a href="${pageContext.request.contextPath}/inbox" class="btn btn-outline-primary btn-sm">View Inbox</a>
    <a href="${pageContext.request.contextPath}/inbox?level=CRITICAL" class="btn btn-outline-danger btn-sm">View Critical</a>
  </div>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
