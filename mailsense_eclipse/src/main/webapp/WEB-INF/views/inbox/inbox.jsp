<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8"/>
  <meta name="viewport" content="width=device-width, initial-scale=1"/>
  <title>Inbox — MailSense</title>
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"/>
  <style>
    body { background:#f8fafc; }
    .email-row { cursor:pointer; border-left:4px solid transparent; transition:background .15s; }
    .email-row:hover { background:#eff6ff; }
    .email-row.unread { font-weight:600; border-left-color:#2563eb; }
    .email-row.CRITICAL { border-left-color:#dc2626; }
    .email-row.HIGH     { border-left-color:#ea580c; }
    .email-row.MEDIUM   { border-left-color:#ca8a04; }
    .badge-CRITICAL { background:#fef2f2;color:#dc2626;border:1px solid #fca5a5; }
    .badge-HIGH     { background:#fff7ed;color:#ea580c;border:1px solid #fdba74; }
    .badge-MEDIUM   { background:#fefce8;color:#ca8a04;border:1px solid #fde047; }
    .badge-LOW      { background:#f0fdf4;color:#16a34a;border:1px solid #86efac; }
    .tag-pill { font-size:.7rem;padding:2px 8px;border-radius:999px;
                background:#dbeafe;color:#1d4ed8;font-weight:600; }
    .preview  { color:#64748b;font-size:.85rem; }
  </style>
</head>
<body>
<%@ include file="../_navbar.jsp" %>

<div class="container-fluid py-4" style="max-width:1100px">

  <c:if test="${snoozeSuccess}">
    <div class="alert alert-success alert-dismissible fade show">
      Email snoozed.
      <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
  </c:if>
  <c:if test="${sendSuccess}">
    <div class="alert alert-success alert-dismissible fade show">
      Email sent successfully.
      <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
  </c:if>

  <%-- Stats --%>
  <div class="row g-3 mb-4">
    <div class="col-6 col-md-3">
      <div class="card border-0 shadow-sm p-3 text-center">
        <div class="fs-3 fw-bold text-primary">${unreadCount}</div>
        <div class="text-muted small">Unread</div>
      </div>
    </div>
    <div class="col-6 col-md-3">
      <div class="card border-0 shadow-sm p-3 text-center">
        <div class="fs-3 fw-bold text-danger">${criticalCount}</div>
        <div class="text-muted small">Critical</div>
      </div>
    </div>
    <div class="col-md-6">
      <div class="card border-0 shadow-sm p-3 d-flex flex-row flex-wrap gap-2 align-items-center">
        <span class="text-muted small">Tags:</span>
        <a href="${pageContext.request.contextPath}/inbox"
           class="tag-pill text-decoration-none ${empty currentTag ? 'bg-primary text-white' : ''}">All</a>
        <c:forEach var="t" items="${['URGENT','MEETING','PAYMENT','SUPPORT','FINANCE','HIGH_PRIORITY']}">
          <a href="${pageContext.request.contextPath}/inbox?tag=${t}"
             class="tag-pill text-decoration-none ${currentTag eq t ? 'bg-primary text-white' : ''}">${t}</a>
        </c:forEach>
      </div>
    </div>
  </div>

  <%-- Email list --%>
  <div class="card border-0 shadow-sm" style="border-radius:12px">
    <div class="card-header bg-white border-bottom py-3 d-flex justify-content-between align-items-center">
      <h5 class="mb-0">
        Inbox
        <c:if test="${not empty currentTag}">
          <span class="badge bg-primary ms-2">${currentTag}</span>
        </c:if>
      </h5>
      <a href="${pageContext.request.contextPath}/emails/compose"
         class="btn btn-sm btn-primary">+ Compose</a>
    </div>

    <div class="list-group list-group-flush">
      <c:choose>
        <c:when test="${empty emails}">
          <div class="text-center py-5 text-muted">
            <div style="font-size:3rem;opacity:.2">&#9993;</div>
            <p>No emails found.</p>
          </div>
        </c:when>
        <c:otherwise>
          <c:forEach var="email" items="${emails}">
            <div class="list-group-item list-group-item-action email-row
                        ${email.read ? '' : 'unread'} ${email.urgencyLevel}"
                 onclick="location='${pageContext.request.contextPath}/emails/${email.id}'">
              <div class="d-flex w-100 justify-content-between align-items-start py-1">
                <div class="flex-grow-1 pe-3 overflow-hidden">
                  <div class="d-flex align-items-center gap-2 mb-1 flex-wrap">
                    <span class="badge badge-${email.urgencyLevel} rounded-pill">${email.urgencyLevel}</span>
                    <c:if test="${!email.read}"><span class="text-primary">&#9679;</span></c:if>
                    <span class="fw-semibold text-truncate" style="max-width:200px">${email.fromAddress}</span>
                    <c:forEach var="tag" items="${email.tags}">
                      <span class="tag-pill">${tag.tagName}</span>
                    </c:forEach>
                    <c:if test="${email.hasAttachment}"><span title="Attachment">&#128206;</span></c:if>
                  </div>
                  <div class="text-truncate">${email.subject}</div>
                  <div class="preview text-truncate">${email.bodyPreview}</div>
                </div>
                <div class="text-end text-nowrap ms-2">
                  <div class="small fw-bold
                    ${email.urgencyScore >= 80 ? 'text-danger' :
                      email.urgencyScore >= 50 ? 'text-warning' : 'text-muted'}">
                    ${email.urgencyScore}
                  </div>
                  <div class="text-muted" style="font-size:.78rem">
                    <fmt:formatDate value="${email.receivedAt}" pattern="dd MMM HH:mm"/>
                  </div>
                  <button class="btn btn-sm btn-outline-secondary mt-1 py-0"
                          title="Snooze"
                          onclick="event.stopPropagation();openSnooze(${email.id})">&#128336;</button>
                </div>
              </div>
            </div>
          </c:forEach>
        </c:otherwise>
      </c:choose>
    </div>

    <div class="card-footer bg-white py-2 d-flex justify-content-between align-items-center">
      <c:if test="${currentPage > 0}">
        <a href="?page=${currentPage-1}<c:if test='${not empty currentTag}'>&tag=${currentTag}</c:if>"
           class="btn btn-sm btn-outline-primary">&larr; Prev</a>
      </c:if>
      <span class="text-muted small mx-auto">Page ${currentPage+1}</span>
      <c:if test="${emails.size() == pageSize}">
        <a href="?page=${currentPage+1}<c:if test='${not empty currentTag}'>&tag=${currentTag}</c:if>"
           class="btn btn-sm btn-outline-primary">Next &rarr;</a>
      </c:if>
    </div>
  </div>
</div>

<%-- Snooze Modal --%>
<div class="modal fade" id="snoozeModal" tabindex="-1">
  <div class="modal-dialog modal-sm">
    <div class="modal-content">
      <div class="modal-header">
        <h6 class="modal-title">Snooze Email</h6>
        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
      </div>
      <form id="snoozeForm" method="post">
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
        <div class="modal-body">
          <label class="form-label small">Wake up at:</label>
          <input type="datetime-local" name="snoozeUntil" id="snoozeInput" class="form-control form-control-sm"/>
        </div>
        <div class="modal-footer py-2">
          <button type="submit" class="btn btn-sm btn-primary w-100">Snooze</button>
        </div>
      </form>
    </div>
  </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
<script>
function openSnooze(id) {
  var d = new Date(); d.setDate(d.getDate()+1); d.setHours(9,0,0,0);
  document.getElementById('snoozeInput').value = d.toISOString().slice(0,16);
  document.getElementById('snoozeForm').action =
    '${pageContext.request.contextPath}/emails/'+id+'/snooze';
  new bootstrap.Modal(document.getElementById('snoozeModal')).show();
}
</script>
</body>
</html>
