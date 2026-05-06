<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Register - Smart Email Analyzer</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css">
</head>
<body class="auth-body">
<div class="page-container auth-container">
    <%@ include file="common/header.jspf" %>

    <div class="auth-layout register-layout">
        <div class="auth-showcase">
            <span class="section-kicker">Get Started</span>
            <h2>Create your workspace for Gmail analysis.</h2>
            <p>
                Register once, store your Gmail app password securely in the database,
                and start organizing your inbox with smart tags and urgency scoring.
            </p>

            <div class="feature-list">
                <span class="feature-pill">Gmail IMAP Ready</span>
                <span class="feature-pill">Personal Rules</span>
                <span class="feature-pill">Urgent Mail Detection</span>
                <span class="feature-pill">Daily Dashboard</span>
            </div>
        </div>

        <div class="auth-card wide-card">
            <span class="section-kicker">Register</span>
            <h2>Create a new account</h2>
            <p class="muted-text">Add your login password and Gmail App Password for IMAP access.</p>

            <form action="${pageContext.request.contextPath}/register" method="post" class="form-stack auth-form">
                <div class="form-field">
                    <label for="name">Full Name</label>
                    <input id="name" type="text" name="name" value="${registrationForm.name}" placeholder="Enter your name" autocomplete="name" required>
                </div>

                <div class="form-field">
                    <label for="email">Email Address</label>
                    <input id="email" type="email" name="email" value="${registrationForm.email}" placeholder="yourgmail@gmail.com" autocomplete="email" required>
                </div>

                <div class="form-field">
                    <label for="password">Application Password</label>
                    <input id="password" type="password" name="password" placeholder="Password for app login" autocomplete="new-password" required>
                </div>

                <div class="form-field">
                    <label for="googleAppPassword">Google App Password</label>
                    <input id="googleAppPassword" type="password" name="googleAppPassword" placeholder="16-character Gmail app password" required>
                </div>

                <div class="form-actions">
                    <button type="submit" class="btn btn-primary">Create Account</button>
                </div>
            </form>

            <p class="helper-link">Already registered? <a href="${pageContext.request.contextPath}/login">Go to login</a></p>
        </div>
    </div>
</div>
</body>
</html>
