<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8"/>
  <meta name="viewport" content="width=device-width, initial-scale=1"/>
  <title>${email.subject} — MailSense</title>
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"/>
  <style>
    body { background:#f8fafc; }
    .email-body { white-space:pre-wrap;line-height:1.7;font-size:.95rem; }
    .badge-CRITICAL{background:#fef2f2;color:#dc2626;border:1px solid #fca5a5}
    .badge-HIGH    {background:#fff7ed;color:#ea580c;border:1px solid #fdba74}
    .badge-MEDIUM  {background:#fefce8;color:#ca8a04;border:1px solid #fde047}
    .badge-LOW     {background:#f0fdf4;color:#16a34a;border:1px solid #86efac}
    .tag-pill{font-size:.7rem;padding:2px 8px;border-radius:999px;background:#dbeafe;color:#1d4ed8;font-weight:600}
  </style>
</head>
<body>
<%@ include file="../_navbar.jsp" %>

<div class="container py-4" style="max-width:860px">

  <c:if test="${sendSuccess}">
    <div class="alert alert-success alert-dismissible fade show">
      Email sent successfully.
      <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
  </c:if>
  <c:if test="${not empty sendError}">
    <div class="alert alert-danger alert-dismissible fade show">
      ${sendError}
      <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
  </c:if>

  <a href="${pageContext.request.contextPath}/inbox" class="btn btn-sm btn-outline-secondary mb-3">
    &larr; Back to Inbox
  </a>

  <div class="card shadow-sm border-0" style="border-radius:12px">
    <div class="card-header bg-white border-bottom p-4">
      <h4 class="mb-3">${email.subject}</h4>
      <div class="d-flex flex-wrap gap-4 align-items-center">
        <div><small class="text-muted">From</small><br/><strong>${email.fromAddress}</strong></div>
        <div>
          <small class="text-muted">Date</small><br/>
          <strong><fmt:formatDate value="${email.receivedAt}" pattern="dd MMM yyyy, HH:mm"/></strong>
        </div>
        <div class="ms-auto">
          <span class="badge badge-${email.urgencyLevel} fs-6 px-3 py-2">
            ${email.urgencyLevel} — Score: ${email.urgencyScore}
          </span>
        </div>
      </div>

      <%-- Score bar --%>
      <div class="mt-3">
        <div class="d-flex justify-content-between small text-muted mb-1">
          <span>Urgency</span><span>${email.urgencyScore}/100</span>
        </div>
        <div class="progress" style="height:6px">
          <div class="progress-bar
            ${email.urgencyScore>=80?'bg-danger':email.urgencyScore>=50?'bg-warning':email.urgencyScore>=30?'bg-info':'bg-success'}"
               style="width:${email.urgencyScore}%"></div>
        </div>
      </div>

      <c:if test="${not empty email.tags}">
        <div class="mt-3 d-flex gap-2 flex-wrap">
          <c:forEach var="t" items="${email.tags}">
            <span class="tag-pill">${t.tagName}</span>
          </c:forEach>
        </div>
      </c:if>
    </div>

    <div class="card-body p-4">
      <div class="email-body">${email.fullBody}</div>
    </div>

    <div class="card-footer bg-white border-top p-3 d-flex gap-2 flex-wrap">
      <button class="btn btn-primary btn-sm"
              data-bs-toggle="modal" data-bs-target="#replyModal">Reply</button>
      <button class="btn btn-outline-primary btn-sm"
              data-bs-toggle="modal" data-bs-target="#forwardModal">Forward</button>
      <button class="btn btn-outline-secondary btn-sm"
              data-bs-toggle="modal" data-bs-target="#snoozeModal">Snooze</button>
    </div>
  </div>
</div>

<%-- Reply Modal --%>
<div class="modal fade" id="replyModal" tabindex="-1">
  <div class="modal-dialog modal-lg">
    <div class="modal-content">
      <div class="modal-header"><h6 class="modal-title">Reply</h6>
        <button type="button" class="btn-close" data-bs-dismiss="modal"></button></div>
      <form action="${pageContext.request.contextPath}/emails/send" method="post">
        <input type="hidden" name="${_csrf.parameterName}"   value="${_csrf.token}"/>
        <input type="hidden" name="sendType"                 value="REPLY"/>
        <input type="hidden" name="inReplyToEmailId"         value="${email.id}"/>
        <input type="hidden" name="inReplyToMessageId"       value="${email.messageId}"/>
        <div class="modal-body">
          <div class="mb-2">
            <label class="form-label small">To</label>
            <input type="email" name="toAddress" class="form-control form-control-sm"
                   value="${email.fromAddress}" readonly/>
          </div>
          <div class="mb-2">
            <label class="form-label small">Subject</label>
            <input type="text" name="subject" class="form-control form-control-sm"
                   value="Re: ${email.subject}"/>
          </div>
          <div class="mb-2">
            <label class="form-label small">Message</label>
            <textarea name="body" class="form-control" rows="6" placeholder="Write reply..."></textarea>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn btn-sm btn-secondary" data-bs-dismiss="modal">Cancel</button>
          <button type="submit" class="btn btn-sm btn-primary">Send Reply</button>
        </div>
      </form>
    </div>
  </div>
</div>

<%-- Forward Modal --%>
<div class="modal fade" id="forwardModal" tabindex="-1">
  <div class="modal-dialog modal-lg">
    <div class="modal-content">
      <div class="modal-header"><h6 class="modal-title">Forward</h6>
        <button type="button" class="btn-close" data-bs-dismiss="modal"></button></div>
      <form action="${pageContext.request.contextPath}/emails/send" method="post">
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
        <input type="hidden" name="sendType"         value="FORWARD"/>
        <input type="hidden" name="inReplyToEmailId" value="${email.id}"/>
        <input type="hidden" name="originalBody"     value="${email.fullBody}"/>
        <div class="modal-body">
          <div class="mb-2">
            <label class="form-label small">To</label>
            <input type="email" name="toAddress" class="form-control form-control-sm" required/>
          </div>
          <div class="mb-2">
            <label class="form-label small">CC</label>
            <input type="text" name="ccAddress" class="form-control form-control-sm"/>
          </div>
          <div class="mb-2">
            <label class="form-label small">Subject</label>
            <input type="text" name="subject" class="form-control form-control-sm"
                   value="Fwd: ${email.subject}"/>
          </div>
          <div class="mb-2">
            <label class="form-label small">Add note</label>
            <textarea name="body" class="form-control" rows="3"></textarea>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn btn-sm btn-secondary" data-bs-dismiss="modal">Cancel</button>
          <button type="submit" class="btn btn-sm btn-primary">Forward</button>
        </div>
      </form>
    </div>
  </div>
</div>

<%-- Snooze Modal --%>
<div class="modal fade" id="snoozeModal" tabindex="-1">
  <div class="modal-dialog modal-sm">
    <div class="modal-content">
      <div class="modal-header"><h6 class="modal-title">Snooze</h6>
        <button type="button" class="btn-close" data-bs-dismiss="modal"></button></div>
      <form action="${pageContext.request.contextPath}/emails/${email.id}/snooze" method="post">
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
        <div class="modal-body">
          <input type="datetime-local" name="snoozeUntil" id="snoozeAt" class="form-control form-control-sm"/>
          <div class="d-flex gap-2 mt-2 flex-wrap">
            <button type="button" class="btn btn-outline-secondary btn-sm"
                    onclick="setSnooze(1,9)">Tomorrow 9AM</button>
            <button type="button" class="btn btn-outline-secondary btn-sm"
                    onclick="setSnooze(0,18)">Tonight 6PM</button>
            <button type="button" class="btn btn-outline-secondary btn-sm"
                    onclick="setSnooze(7,9)">Next Week</button>
          </div>
        </div>
        <div class="modal-footer py-2">
          <button type="submit" class="btn btn-sm btn-warning w-100">Snooze</button>
        </div>
      </form>
    </div>
  </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
<script>
function setSnooze(days, hour) {
  var d = new Date(); d.setDate(d.getDate()+days); d.setHours(hour,0,0,0);
  document.getElementById('snoozeAt').value = d.toISOString().slice(0,16);
}
setSnooze(1,9);
</script>
</body>
</html>
