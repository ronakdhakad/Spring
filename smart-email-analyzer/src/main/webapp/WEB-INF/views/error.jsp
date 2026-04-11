<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Error – Smart Email Analyzer</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css">
</head>
<body class="login-page">
<div class="login-container">
    <div class="login-card" style="text-align:center;">
        <div style="font-size:4rem;"></div>
        <h2>Oops! Something went wrong.</h2>
        <p>Error code: <strong>${pageContext.errorData.statusCode}</strong></p>
        <p>${pageContext.errorData.throwable.message}</p>
        <br>
        <a href="${pageContext.request.contextPath}/inbox" class="btn btn-primary">Back to Inbox</a>
        <a href="${pageContext.request.contextPath}/login" class="btn btn-secondary">Login</a>
    </div>
</div>
</body>
</html>
