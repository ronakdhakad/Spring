<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c"    uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8"/>
  <meta name="viewport" content="width=device-width, initial-scale=1"/>
  <title>Register — MailSense</title>
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"/>
  <style>
    body { background:#f0f4ff; }
    .card { max-width:560px; margin:40px auto; border-radius:16px;
            box-shadow:0 4px 24px rgba(37,99,235,.12); }
    .card-header { background:linear-gradient(135deg,#2563eb,#1e40af);
                   color:#fff; border-radius:16px 16px 0 0; padding:20px 32px; }
    .section-label { font-size:.75rem; font-weight:700; letter-spacing:.08em;
                     color:#64748b; text-transform:uppercase; margin:20px 0 8px; }
  </style>
</head>
<body>
<div class="card border-0">
  <div class="card-header">
    <h4 class="mb-0 fw-bold">&#9993; Create Account</h4>
    <small class="opacity-75">Connect your Gmail to MailSense</small>
  </div>
  <div class="card-body p-4">

    <c:if test="${not empty registerError}">
      <div class="alert alert-danger">${registerError}</div>
    </c:if>
    <c:if test="${pwdMismatch}">
      <div class="alert alert-danger">Passwords do not match.</div>
    </c:if>

    <form:form modelAttribute="registerDto"
               action="${pageContext.request.contextPath}/auth/register"
               method="post">

      <div class="section-label">Account Details</div>

      <div class="mb-3">
        <label class="form-label">Full Name</label>
        <form:input path="fullName" cssClass="form-control" placeholder="Your full name"/>
        <form:errors path="fullName" cssClass="text-danger small"/>
      </div>
      <div class="mb-3">
        <label class="form-label">Login Email</label>
        <form:input path="email" cssClass="form-control" placeholder="your@email.com"/>
        <form:errors path="email" cssClass="text-danger small"/>
      </div>
      <div class="row">
        <div class="col-md-6 mb-3">
          <label class="form-label">Password</label>
          <form:password path="password" cssClass="form-control" placeholder="Min 8 chars"/>
          <form:errors path="password" cssClass="text-danger small"/>
        </div>
        <div class="col-md-6 mb-3">
          <label class="form-label">Confirm Password</label>
          <form:password path="confirmPassword" cssClass="form-control" placeholder="Repeat password"/>
        </div>
      </div>

      <div class="section-label">Gmail Connection</div>
      <div class="alert alert-info py-2 small">
        Gmail App Password is a 16-char code from Google Account security (requires 2FA).
        <a href="https://myaccount.google.com/apppasswords" target="_blank">Generate here</a>.
      </div>

      <div class="mb-3">
        <label class="form-label">Gmail Address</label>
        <form:input path="gmailAddress" cssClass="form-control" placeholder="you@gmail.com"/>
        <form:errors path="gmailAddress" cssClass="text-danger small"/>
      </div>
      <div class="mb-3">
        <label class="form-label">
          Gmail App Password
          <span class="badge bg-warning text-dark ms-1">Encrypted in DB</span>
        </label>
        <form:password path="gmailAppPassword" cssClass="form-control font-monospace"
                       placeholder="xxxxxxxxxxxxxxxx"/>
        <div class="form-text">16 characters, no spaces. Stored encrypted.</div>
        <form:errors path="gmailAppPassword" cssClass="text-danger small"/>
      </div>

      <div class="section-label">
        <a class="text-muted text-decoration-none" data-bs-toggle="collapse" href="#advConf">
          &#9660; Advanced IMAP/SMTP Settings
        </a>
      </div>
      <div class="collapse" id="advConf">
        <div class="row">
          <div class="col-8 mb-2">
            <label class="form-label small">IMAP Host</label>
            <form:input path="imapHost" cssClass="form-control form-control-sm"/>
          </div>
          <div class="col-4 mb-2">
            <label class="form-label small">IMAP Port</label>
            <form:input path="imapPort" cssClass="form-control form-control-sm" type="number"/>
          </div>
          <div class="col-8 mb-2">
            <label class="form-label small">SMTP Host</label>
            <form:input path="smtpHost" cssClass="form-control form-control-sm"/>
          </div>
          <div class="col-4 mb-2">
            <label class="form-label small">SMTP Port</label>
            <form:input path="smtpPort" cssClass="form-control form-control-sm" type="number"/>
          </div>
        </div>
      </div>

      <button type="submit" class="btn btn-primary w-100 py-2 fw-semibold mt-3">
        Create Account
      </button>
    </form:form>

    <hr/>
    <p class="text-center mb-0">
      Already have an account?
      <a href="${pageContext.request.contextPath}/auth/login" class="fw-semibold">Sign in</a>
    </p>
  </div>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
