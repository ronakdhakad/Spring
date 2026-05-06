<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Login - Smart Email Analyzer</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css">
</head>
<body class="auth-body">
<div class="page-container auth-container">
    <%@ include file="common/header.jspf" %>

    <div class="auth-card">
        <h2>Login</h2>
        <p class="muted-text">Use the email and password registered in the application.</p>

        <form action="${pageContext.request.contextPath}/login" method="post" class="form-grid">
            <label>Email</label>
            <input type="email" name="email" value="${loginForm.email}" placeholder="you@gmail.com" required>

            <label>Password</label>
            <input type="password" name="password" placeholder="Enter password" required>

            <button type="submit" class="btn btn-primary">Login</button>
        </form>

        <p class="helper-link">New user? <a href="${pageContext.request.contextPath}/register">Create account</a></p>
    </div>

    <%@ include file="common/footer.jspf" %>
</div>
</body>
</html>
