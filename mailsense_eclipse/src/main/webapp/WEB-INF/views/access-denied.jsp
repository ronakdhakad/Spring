<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html><html><head><meta charset="UTF-8"/>
<title>Access Denied</title>
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"/>
<style>body{background:#f8fafc}</style></head>
<body><div class="text-center mt-5">
  <div style="font-size:4rem">&#128274;</div>
  <h2 class="mt-3 fw-bold">Access Denied</h2>
  <p class="text-muted">You don't have permission to access this page.</p>
  <a href="${pageContext.request.contextPath}/inbox" class="btn btn-primary mt-2">Go to Inbox</a>
</div></body></html>
