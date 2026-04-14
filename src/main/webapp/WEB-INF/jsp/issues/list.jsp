<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <title>Lista de Chamados</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<div class="container mt-5">
    <h2>Lista de Chamados</h2>
    <c:if test="${not empty errorMessage}">
        <div class="alert alert-danger"><c:out value="${errorMessage}"/></div>
    </c:if>

    <div class="d-flex justify-content-between mb-3">
        <div>
            <a href="${pageContext.request.contextPath}/issues/nova" class="btn btn-success">Abrir Novo Chamado</a>

            <sec:authorize access="hasRole('ADMIN')">
                <a href="${pageContext.request.contextPath}/admin/tipos-chamados" class="btn btn-outline-dark ms-2">
                    <i class="bi bi-gear"></i> Gerenciar Tipos
                </a>
                <a href="${pageContext.request.contextPath}/admin/status-chamados" class="btn btn-outline-dark ms-2">
                    <i class="bi bi-list-check"></i> Fluxo de Status
                </a>
            </sec:authorize>

            <a href="${pageContext.request.contextPath}/" class="btn btn-secondary ms-2">Voltar</a>
        </div>

        <form action="${pageContext.request.contextPath}/issues" method="get" class="d-flex" style="width: 400px;">
            <input type="text" name="q" class="form-control me-2" placeholder="Pesquisar..." value="${keyword}">
            <button type="submit" class="btn btn-primary">Pesquisar</button>
            <c:if test="${not empty keyword}">
                <a href="${pageContext.request.contextPath}/issues" class="btn btn-outline-secondary ms-2">Limpar</a>
            </c:if>
        </form>
    </div>

    <c:choose>
        <c:when test="${empty issues}">
            <div class="alert alert-info mt-3">Nenhum chamado encontrado no seu escopo de acesso.</div>
        </c:when>
        <c:otherwise>
            <div class="row mt-3">
                <c:forEach var="issue" items="${issues}">
                    <div class="col-12 mb-4">
                        <div class="card shadow-sm">
                            <div class="card-header d-flex justify-content-between align-items-center">
                                <div>
                                    <strong>Chamado #<c:out value="${issue.id}"/> - <c:out value="${issue.title}"/></strong>
                                    <span class="badge bg-secondary ms-2">
                                        <i class="bi bi-tag-fill"><c:out value="${issue.type.title}"/></i>
                                    </span>
                                </div>

                                <span class="badge ${issue.status.isFinal ? 'bg-success' : 'bg-warning text-dark'}">
                                    <c:out value="${issue.status.name}" />
                                </span>
                            </div>
                            <div class="card-body">
                                <p class="card-text"><c:out value="${issue.description}" /></p>

                                <c:if test="${not empty issue.attachments}">
                                    <div class="mb-3 p-2 bg-light border rounded">
                                        <strong>Anexos:</strong><br>
                                        <div class="d-flex flex-wrap gap-2 mt-1">
                                            <c:forEach var="attachment" items="${issue.attachments}" varStatus="status">
                                                <a href="${pageContext.request.contextPath}${attachment.fileUrl}" target="_blank" class="btn btn-sm btn-outline-secondary">
                                                    Ver Arquivo ${status.index + 1}
                                                </a>
                                            </c:forEach>
                                        </div>
                                    </div>
                                </c:if>

                                <div class="text-muted small mb-3">
                                    <div><strong>Unidade:</strong> <c:out value="${issue.unit.identifier}"/></div>
                                    <div><strong>Aberto por:</strong> <c:out value="${issue.createdBy.name}"/> <i>(<c:out value="${issue.createdBy.role}"/>)</i></div>
                                    <div><strong>Data de Abertura:</strong> <c:out value="${issue.formattedCreatedAt}"/></div>

                                    <div>
                                        <strong>Prazo Limite:</strong> <c:out value="${issue.formattedDeadline}"/>
                                        <c:out value="${issue.slaStatusBadge}" escapeXml="false" />
                                    </div>

                                    <c:if test="${issue.status.isFinal}">
                                        <div class="text-success"><strong>Data de Conclusão:</strong> <c:out value="${issue.formattedFinishedAt}"/></div>
                                    </c:if>
                                </div>

                                <c:choose>
                                    <c:when test="${issue.status.isFinal}">
                                    </c:when>
                                    <c:otherwise>
                                        <c:if test="${currentUser.role != 'MORADOR'}">
                                            <form action="${pageContext.request.contextPath}/issues/${issue.id}/status" method="post" class="d-flex mb-3 form-update-status">
                                                <select name="newStatusId" class="form-select form-select-sm me-2 status-select" style="width: auto;" required>
                                                    <option value="" disabled selected>Alterar Status...</option>
                                                    <c:forEach var="status" items="${statuses}">
                                                        <option value="${status.id}"
                                                                data-isfinal="${status.isFinal}"${issue.status.id == status.id ? 'selected' : ''}>
                                                            <c:out value="${status.name}"/>
                                                        </option>
                                                    </c:forEach>
                                                </select>
                                                <button type="submit" class="btn btn-sm btn-outline-dark">Atualizar</button>
                                            </form>
                                        </c:if>
                                    </c:otherwise>
                                </c:choose>

                                <hr>

                                <h6>Comentários (<c:out value="${issue.comments.size()}"/>)</h6>
                                <div class="bg-light p-3 rounded mb-3" style="max-height: 200px; overflow-y: auto;">
                                    <c:if test="${empty issue.comments}">
                                        <span class="text-muted small">Nenhum comentário ainda.</span>
                                    </c:if>
                                    <ul class="list-unstyled mb-0">
                                        <c:forEach var="comment" items="${issue.comments}">
                                            <li class="small mb-2">
                                                <strong><c:out value="${comment.author.name}"/></strong> <i>(<c:out value="${comment.author.role}"/>)</i>

                                                <span class="text-muted" style="font-size: 0.8em;"><c:out value="${comment.formattedCreatedAt}"/></span>:
                                                <br>
                                                <c:out value="${comment.content}" />
                                            </li>
                                        </c:forEach>
                                    </ul>
                                </div>

                                <c:choose>
                                    <c:when test="${issue.status.isFinal}">
                                        <div class="alert alert-secondary text-center small p-2 mb-0">
                                            <i class="bi bi-lock-fill"></i> Chamado encerrado. Status e comentários bloqueados.
                                        </div>
                                    </c:when>
                                    <c:otherwise>
                                        <form action="${pageContext.request.contextPath}/issues/${issue.id}/comentarios" method="post" class="d-flex">
                                            <input type="text" name="content" class="form-control form-control-sm me-2" placeholder="Escreva seu comentário..." required>
                                            <button type="submit" class="btn btn-sm btn-primary">Enviar</button>
                                        </form>
                                    </c:otherwise>
                                </c:choose>

                            </div>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </c:otherwise>
    </c:choose>
</div>
<script>
    // Adiciona uma confirmação ao alterar para um status de conclusão de chamado
    document.addEventListener("DOMContentLoaded", function() {
        const forms = document.querySelectorAll('.form-update-status');

        forms.forEach(function(form) {
            form.addEventListener('submit', function(event) {
                const select = form.querySelector('.status-select');
                const selectedOption = select.options[select.selectedIndex];
                const isFinal = selectedOption.getAttribute('data-isfinal') === 'true';

                if (isFinal) {
                    const confirmacao = confirm("Você tem certeza de que deseja marcar esse chamado como concluído? " +
                        "Essa ação não poderá ser desfeita!");
                    if (!confirmacao) {
                        event.preventDefault();
                    }
                }
            });
        });
    });
</script>
</body>
</html>