<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <title>Lista de Blocos</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<div class="container mt-5">
    <h2>Blocos Cadastrados</h2>
    <c:if test="${not empty errorMessage}">
        <div class="alert alert-danger"><c:out value="${errorMessage}"/></div>
    </c:if>

    <div class="d-flex justify-content-between mb-3">
        <div>
            <a href="${pageContext.request.contextPath}/blocos/novo" class="btn btn-success">Novo Bloco</a>
            <a href="${pageContext.request.contextPath}/" class="btn btn-secondary ms-2">Voltar</a>
        </div>

        <form action="${pageContext.request.contextPath}/blocos" method="get" class="d-flex" style="width: 400px;">
            <input type="text" name="q" class="form-control me-2" placeholder="Pesquisar por nome do bloco..." value="${keyword}">
            <button type="submit" class="btn btn-primary">Pesquisar</button>
            <c:if test="${not empty keyword}">
                <a href="${pageContext.request.contextPath}/blocos" class="btn btn-outline-secondary ms-2">Limpar</a>
            </c:if>
        </form>
    </div>

    <table class="table table-striped">
        <thead>
        <tr>
            <th>ID</th>
            <th>Nome</th>
            <th>Andares</th>
            <th>Unidades por andar</th>
            <th>Total de unidades</th>
            <th>Ações</th>
        </tr>
        </thead>
        <tbody>
        <c:choose>
            <c:when test="${empty blocks}">
                <tr>
                    <td colspan="6" class="text-center text-muted">Nenhum bloco encontrado.</td>
                </tr>
            </c:when>
            <c:otherwise>
                <c:forEach var="block" items="${blocks}">
                    <tr>
                        <td><c:out value="${block.id}" /></td>
                        <td><c:out value="${block.identifier}" /></td>
                        <td><c:out value="${block.totalFloors}" /></td>
                        <td><c:out value="${block.apartmentsPerFloor}" /></td>
                        <td><c:out value="${block.units.size()}" /></td>
                        <td>
                            <div class="d-flex gap-2">
                                <a href="${pageContext.request.contextPath}/blocos/${block.id}/editar" class="btn btn-sm btn-warning">Editar</a>
                                <form action="${pageContext.request.contextPath}/blocos/${block.id}/excluir" method="post" onsubmit="return confirm('Tem certeza que deseja excluir este bloco e todas as suas unidades?');">
                                    <button type="submit" class="btn btn-sm btn-danger">Excluir</button>
                                </form>
                            </div>
                        </td>
                    </tr>
                </c:forEach>
            </c:otherwise>
        </c:choose>
        </tbody>
    </table>
</div>
</body>
</html>