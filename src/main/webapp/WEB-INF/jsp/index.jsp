<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <title>Condomínio - Home</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<div class="container mt-5">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h1>Sistema de Gerenciamento de Condomínio</h1>
        <div class="d-flex align-items-center gap-3">
            <span class="text-muted">Olá, <strong><c:out value="${currentUser.name}"/></strong> <c:if test="${currentUser.role == 'ADMIN'}">(<c:out value="${currentUser.role}"/>)</c:if></span>
            <form action="${pageContext.request.contextPath}/logout" method="post" class="m-0">
                <button type="submit" class="btn btn-danger">Sair</button>
            </form>
        </div>
    </div>

    <div class="list-group">
        <c:if test="${currentUser.role == 'ADMIN'}">
            <a href="${pageContext.request.contextPath}/blocos" class="list-group-item list-group-item-action">Gerenciar Blocos e Unidades</a>
            <a href="${pageContext.request.contextPath}/admin/usuarios" class="list-group-item list-group-item-action">Gerenciar Usuários</a>
        </c:if>

        <a href="${pageContext.request.contextPath}/issues" class="list-group-item list-group-item-action">Gerenciar Chamados</a>
    </div>
</div>
</body>
</html>