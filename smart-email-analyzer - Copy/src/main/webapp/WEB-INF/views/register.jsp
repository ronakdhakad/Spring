<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Register - Smart Email Analyzer</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css">
</head>
<body class="auth-body">
<div class="page-container auth-container">
    <%@ include file="common/header.jspf" %>

    <div class="auth-card wide-card">
        <h2>Register</h2>
        <p class="muted-text">Create a user account and store your Gmail App Password for IMAP access.</p>

        <form action="${pageContext.request.contextPath}/register" method="post" class="form-grid">
            <label>Name</label>
            <input type="text" name="name" value="${registrationForm.name}" placeholder="Your name" required>

            <label>Email</label>
            <input type="email" name="email" value="${registrationForm.email}" placeholder="yourgmail@gmail.com" required>

            <label>Application Password</label>
            <input type="password" name="password" placeholder="Password for this app login" required>

            <label>Google App Password</label>
            <input type="password" name="googleAppPassword" placeholder="16-character Gmail app password" required>

            <button type="submit" class="btn btn-primary">Register</button>
        </form>

        <p class="helper-link">Already registered? <a href="${pageContext.request.contextPath}/login">Go to login</a></p>
    </div>

    <%@ include file="common/footer.jspf" %>
</div>
</body>
</html>
