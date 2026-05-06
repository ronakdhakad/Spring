<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<nav class="navbar navbar-expand-lg navbar-dark bg-primary sticky-top shadow-sm">
  <div class="container-fluid">
    <a class="navbar-brand fw-bold" href="${pageContext.request.contextPath}/inbox">
      &#9993; MailSense
    </a>
    <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navMenu">
      <span class="navbar-toggler-icon"></span>
    </button>
    <div class="collapse navbar-collapse" id="navMenu">
      <ul class="navbar-nav me-auto">
        <sec:authorize access="isAuthenticated()">
          <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/inbox">Inbox</a></li>
          <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/rules">Rules</a></li>
          <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/senders">Senders</a></li>
          <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/analytics">Analytics</a></li>
        </sec:authorize>
      </ul>
      <ul class="navbar-nav ms-auto">
        <sec:authorize access="isAuthenticated()">
          <li class="nav-item dropdown">
            <a class="nav-link dropdown-toggle" href="#" data-bs-toggle="dropdown">
              <sec:authentication property="name"/>
            </a>
            <ul class="dropdown-menu dropdown-menu-end">
              <li>
                <form action="${pageContext.request.contextPath}/auth/logout" method="post">
                  <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                  <button class="dropdown-item text-danger" type="submit">Logout</button>
                </form>
              </li>
            </ul>
          </li>
        </sec:authorize>
      </ul>
    </div>
  </div>
</nav>
