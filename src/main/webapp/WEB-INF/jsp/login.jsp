<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <title>Condomínio - Login</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="d-flex align-items-center justify-content-center vh-100 bg-light">
<div class="card p-4 shadow" style="width: 400px;">
    <h3 class="text-center mb-4">Acesso ao Sistema</h3>

    <c:if test="${param.error != null}">
        <div class="alert alert-danger">Email ou senha inválidos.</div>
    </c:if>

    <form action="${pageContext.request.contextPath}/login" method="post">
        <div class="mb-3">
            <label class="form-label w-100">
                Email:
                <input type="email" name="username" class="form-control" required autofocus>
            </label>
        </div>
        <div class="mb-3">
            <label class="form-label w-100">
                Senha:
                <input type="password" name="password" class="form-control" required>
            </label>
        </div>
        <button type="submit" class="btn btn-primary w-100">Entrar</button>
    </form>
</div>
</body>
</html>