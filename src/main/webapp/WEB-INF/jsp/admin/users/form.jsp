<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <title>${user.id == null ? 'Novo Usuário' : 'Editar Usuário'}</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<div class="container mt-5">
    <h2>${user.id == null ? 'Cadastrar Novo Usuário' : 'Editar Usuário'}</h2>

    <c:if test="${not empty errorMessage}">
        <div class="alert alert-danger"><c:out value="${errorMessage}"/></div>
    </c:if>

    <form action="${pageContext.request.contextPath}/admin/usuarios${user.id != null ? '/' += user.id += '/editar' : ''}" method="post">
        <div class="mb-3">
            <label class="form-label">Nome:</label>
            <input type="text" name="name" class="form-control" value="${user.name}" required>
        </div>
        <div class="mb-3">
            <label class="form-label">Email:</label>
            <input type="email" name="email" class="form-control" value="${user.email}" required>
        </div>
        <div class="mb-3">
            <label class="form-label">Telefone (Opcional):</label>
            <input type="text" name="phone" class="form-control" value="${user.phone}" placeholder="(XX) XXXXX-XXXX">
        </div>
        <div class="mb-3">
            <label class="form-label">Senha:</label>
            <input type="password" name="password" class="form-control" ${user.id == null ? 'required' : ''} placeholder="${user.id != null ? 'Deixe em branco para manter a senha atual' : ''}">
        </div>
        <div class="mb-3">
            <label class="form-label">Cargo:</label>
            <select name="role" class="form-select" required>
                <c:forEach var="role" items="${roles}">
                    <option value="${role}" ${user.role == role ? 'selected' : ''}><c:out value="${role}"/></option>
                </c:forEach>
            </select>
        </div>
        <button type="submit" class="btn btn-primary">${user.id == null ? 'Salvar' : 'Atualizar'}</button>
        <a href="${pageContext.request.contextPath}/admin/usuarios" class="btn btn-secondary">Cancelar</a>
    </form>
</div>
</body>
</html>