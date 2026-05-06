<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login - Smart Email Analyzer</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css">
</head>
<body class="auth-body">
<div class="page-container auth-container">
    <%@ include file="common/header.jspf" %>

    <div class="auth-layout">
        <div class="auth-showcase">
            <span class="section-kicker">Welcome Back</span>
            <h2>Focus on the emails that actually need action.</h2>
            <p>
                Smart Email Analyzer helps you fetch Gmail messages, apply custom rules,
                assign urgency scores, and review important conversations from one clean interface.
            </p>

            <div class="feature-list">
                <span class="feature-pill">Urgency Scoring</span>
                <span class="feature-pill">Tag-Based Filtering</span>
                <span class="feature-pill">Rule Engine</span>
                <span class="feature-pill">Inbox Dashboard</span>
            </div>
        </div>

        <div class="auth-card">
            <span class="section-kicker">Login</span>
            <h2>Sign in to your account</h2>
            <p class="muted-text">Use your registered email and application password to continue.</p>

            <form action="${pageContext.request.contextPath}/login" method="post" class="form-stack auth-form">
                <div class="form-field">
                    <label for="email">Email Address</label>
                    <input id="email" type="email" name="email" value="${loginForm.email}" placeholder="Enter your email" autocomplete="email" required>
                </div>

                <div class="form-field">
                    <label for="password">Password</label>
                    <input id="password" type="password" name="password" placeholder="Enter your password" autocomplete="current-password" required>
                </div>

                <div class="form-actions">
                    <button type="submit" class="btn btn-primary">Login Now</button>
                </div>
            </form>

            <p class="helper-link">New user? <a href="${pageContext.request.contextPath}/register">Create account</a></p>
        </div>
    </div>
</div>
</body>
</html>
