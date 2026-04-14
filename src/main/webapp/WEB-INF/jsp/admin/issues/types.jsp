<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <title>Configurar Tipos de Chamados</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<div class="container mt-5">
    <h2>Configuração de Tipos e SLAs</h2>

    <c:if test="${not empty errorMessage}">
        <div class="alert alert-danger"><c:out value="${errorMessage}"/></div>
    </c:if>

    <a href="${pageContext.request.contextPath}/issues" class="btn btn-secondary mb-3">Voltar</a>

    <div class="card mb-4 shadow-sm">
        <div class="card-header">Cadastrar Novo Tipo</div>
        <div class="card-body">
            <form action="${pageContext.request.contextPath}/admin/tipos-chamados" method="post" class="row g-3">
                <div class="col-md-6">
                    <label class="form-label">Título do Tipo:
                        <input type="text" name="title" class="form-control" placeholder="Ex: Manutenção Elétrica" required>
                    </label>
                </div>
                <div class="col-md-4">
                    <label class="form-label">Horas de SLA (Prazo):
                        <input type="number" name="slaHours" class="form-control" placeholder="Ex: 48" min="1" required>
                    </label>
                </div>
                <div class="col-md-2 d-flex align-items-end">
                    <button type="submit" class="btn btn-primary w-100">Adicionar</button>
                </div>
            </form>
        </div>
    </div>

    <table class="table table-hover border bg-white">
        <thead class="table-light">
        <tr>
            <th>ID</th>
            <th>Título</th>
            <th>SLA (em horas)</th>
            <th>Ações</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="type" items="${types}">
            <tr>
                <td>${type.id}</td>
                <td><c:out value="${type.title}"/></td>
                <td>${type.slaHours}h</td>
                <td>
                    <form action="${pageContext.request.contextPath}/admin/tipos-chamados/${type.id}/excluir" method="post" onsubmit="return confirm('Tem certeza? Isso falhará se houver chamados usando este tipo.')">
                        <button type="submit" class="btn btn-sm btn-outline-danger">Remover</button>
                    </form>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div>
</body>
</html>