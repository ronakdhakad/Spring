<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login – Smart Email Analyzer</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css">
</head>
<body class="login-page">

<div class="login-container">
    <div class="login-card">

        <!-- Logo / Title -->
        <div class="login-header">
            <div class="logo">📧</div>
            <h1>Smart Email Analyzer</h1>
            <p>Sign in to access your inbox</p>
        </div>

        <!-- Success / Error Messages -->
        <c:if test="${param.logout == 'true'}">
            <div class="alert alert-success">You have been logged out successfully.</div>
        </c:if>
        <c:if test="${not empty error}">
            <div class="alert alert-error">${error}</div>
        </c:if>

        <!-- Login Form -->
        <form action="${pageContext.request.contextPath}/login" method="post">

            <div class="form-group">
                <label for="email">Email Address</label>
                <input type="email"
                       id="email"
                       name="email"
                       value="${email}"
                       placeholder="you@gmail.com"
                       required
                       autofocus>
            </div>

            <div class="form-group">
                <label for="password">Password</label>
                <input type="password"
                       id="password"
                       name="password"
                       placeholder="Your password"
                       required>
            </div>

            <button type="submit" class="btn btn-primary btn-block">
                Sign In
            </button>

        </form>


    </div>
</div>

</body>
</html>
