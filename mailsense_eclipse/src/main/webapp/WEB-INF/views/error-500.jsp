<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html><html><head><meta charset="UTF-8"/><title>Error</title>
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"/>
<style>body{background:#f8fafc}</style></head>
<body><div class="text-center mt-5">
  <h1 class="display-1 text-danger fw-bold">500</h1>
  <p class="text-muted">Something went wrong. Please try again.</p>
  <a href="${pageContext.request.contextPath}/inbox" class="btn btn-primary">Go to Inbox</a>
</div></body></html>
