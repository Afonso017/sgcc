<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <title>${block.id == null ? 'Novo Bloco' : 'Editar Bloco'}</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<div class="container mt-5">
    <h2>${block.id == null ? 'Cadastrar Novo Bloco' : 'Editar Bloco'}</h2>

    <c:if test="${not empty errorMessage}">
        <div class="alert alert-danger"><c:out value="${errorMessage}"/></div>
    </c:if>

    <form action="${pageContext.request.contextPath}/admin/blocos${block.id != null ? '/' += block.id += '/editar' : ''}"
          method="post">
        <div class="mb-3">
            <label class="form-label">Nome do bloco:</label>
            <input type="text" name="identifier" class="form-control" value="${block.identifier}" required>
        </div>

        <div class="mb-3">
            <label class="form-label">Quantidade de andares:</label>
            <input type="number" name="totalFloors" class="form-control" value="${block.totalFloors}" required min="1"
                   max="1000" ${block.id != null ? 'readonly' : ''}>
            <c:choose>
                <c:when test="${block.id != null}">
                    <small class="text-muted">Não é possível alterar andares de um bloco já criado.</small>
                </c:when>
                <c:otherwise>
                    <small class="text-muted">A arquitetura vertical limita a operação em no máximo 1000
                        andares.</small>
                </c:otherwise>
            </c:choose>
        </div>
        <div class="mb-3">
            <label class="form-label">Unidades por andar:</label>
            <input type="number" name="apartmentsPerFloor" class="form-control" value="${block.apartmentsPerFloor}"
                   required min="1" max="10000" ${block.id != null ? 'readonly' : ''}>
            <c:choose>
                <c:when test="${block.id != null}">
                    <small class="text-muted">Não é possível alterar unidades de um bloco já criado.</small>
                </c:when>
                <c:otherwise>
                    <small class="text-muted">A geração suporta no máximo 10000 unidades por andar.</small>
                </c:otherwise>
            </c:choose>
        </div>
        <button type="submit" class="btn btn-primary">${block.id == null ? 'Cadastrar' : 'Atualizar Bloco'}</button>
        <a href="${pageContext.request.contextPath}/admin/blocos" class="btn btn-secondary">Cancelar</a>
    </form>
</div>
</body>
</html>