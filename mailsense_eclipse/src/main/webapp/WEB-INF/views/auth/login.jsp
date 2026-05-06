<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8"/>
  <meta name="viewport" content="width=device-width, initial-scale=1"/>
  <title>Login — MailSense</title>
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"/>
  <style>
    body { background: #f0f4ff; }
    .card { max-width: 420px; margin: 80px auto; border-radius: 16px;
            box-shadow: 0 4px 24px rgba(37,99,235,.12); }
    .card-header { background: linear-gradient(135deg,#2563eb,#1e40af);
                   color:#fff; border-radius: 16px 16px 0 0; padding: 28px 32px 20px; }
  </style>
</head>
<body>
<div class="card border-0">
  <div class="card-header text-center">
    <div style="font-size:2.5rem">&#9993;</div>
    <h3 class="mt-1 mb-0 fw-bold">MailSense</h3>
    <small class="opacity-75">Smart Inbox Intelligence</small>
  </div>
  <div class="card-body p-4">

    <c:if test="${loginError}">
      <div class="alert alert-danger">Invalid email or password.</div>
    </c:if>
    <c:if test="${logoutSuccess}">
      <div class="alert alert-success">You have been logged out.</div>
    </c:if>
    <c:if test="${registered}">
      <div class="alert alert-success">Registration successful! Please log in.</div>
    </c:if>
    <c:if test="${sessionExpired}">
      <div class="alert alert-warning">Session expired. Please log in again.</div>
    </c:if>

    <form action="${pageContext.request.contextPath}/auth/login" method="post">
      <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>

      <div class="mb-3">
        <label class="form-label fw-semibold">Email address</label>
        <input type="email" name="email" class="form-control"
               placeholder="you@gmail.com" required autofocus/>
      </div>
      <div class="mb-3">
        <label class="form-label fw-semibold">Password</label>
        <input type="password" name="password" class="form-control"
               placeholder="••••••••" required/>
      </div>
      <div class="mb-3 form-check">
        <input type="checkbox" class="form-check-input" id="rememberMe" name="remember-me"/>
        <label class="form-check-label" for="rememberMe">Remember me for 7 days</label>
      </div>
      <button type="submit" class="btn btn-primary w-100 py-2 fw-semibold">Sign In</button>
    </form>

    <hr/>
    <p class="text-center mb-0">
      No account?
      <a href="${pageContext.request.contextPath}/auth/register" class="fw-semibold">Register here</a>
    </p>
  </div>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
