<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <title>Configurar Status de Chamados</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<div class="container mt-5">
    <h2>Fluxo de Status</h2>

    <c:if test="${not empty errorMessage}">
        <div class="alert alert-danger"><c:out value="${errorMessage}"/></div>
    </c:if>

    <a href="${pageContext.request.contextPath}/issues" class="btn btn-secondary mb-3">Voltar</a>

    <div class="card mb-4 shadow-sm">
        <div class="card-header">Cadastrar Novo Status</div>
        <div class="card-body">
            <form action="${pageContext.request.contextPath}/admin/status-chamados" method="post" class="row g-3">
                <div class="col-md-4">
                    <label class="form-label">Nome do Status:
                        <input type="text" name="name" class="form-control" placeholder="Ex: Em Análise" required>
                    </label>
                </div>

                <div class="col-md-5 mt-4">
                    <label class="form-label me-2">Selecione o tipo de status (não obrigatório):
                        <select name="configType" class="form-select form-select-sm d-inline-block w-auto">
                            <option value="NONE">Nenhum</option>
                            <option value="START">Abertura (Padrão)</option>
                            <option value="END">Conclusão</option>
                        </select>
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
            <th>Status</th>
            <th>Configurações</th>
            <th>Ações</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="status" items="${statuses}">
            <tr>
                <td>${status.id}</td>
                <td><c:out value="${status.name}"/></td>
                <td>
                    <c:if test="${status.isDefault}"><span class="badge bg-primary">Abertura</span></c:if>
                    <c:if test="${status.isFinal}"><span class="badge bg-success">Conclusão</span></c:if>
                </td>
                <td>
                    <form action="${pageContext.request.contextPath}/admin/status-chamados/${status.id}/excluir" method="post" onsubmit="return confirm('Atenção: Excluir pode afetar chamados em andamento.')">
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