<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<nav class="navbar">
    <div class="navbar-brand">
        <a href="${pageContext.request.contextPath}/inbox"> Smart Email Analyzer</a>
    </div>
    <ul class="navbar-links">
        <li><a href="${pageContext.request.contextPath}/inbox"     class="${pageTitle == 'Inbox'       ? 'active' : ''}">\ Inbox</a></li>
        <li><a href="${pageContext.request.contextPath}/filter"    class="${pageTitle == 'Filter & Sort' ? 'active' : ''}"> Filter</a></li>
        <li><a href="${pageContext.request.contextPath}/dashboard" class="${pageTitle == 'Dashboard'   ? 'active' : ''}"> Dashboard</a></li>
        <li><a href="${pageContext.request.contextPath}/rules"     class="${pageTitle == 'Rule Management' ? 'active' : ''}"> Rules</a></li>
    </ul>
    <div class="navbar-user">
        <span> ${user.email}</span>
        <a href="${pageContext.request.contextPath}/logout" class="btn btn-small btn-danger">Logout</a>
    </div>
</nav>
