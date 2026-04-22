<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <title>Lista de Usuários</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<div class="container mt-5">
    <h2>Usuários Cadastrados</h2>

    <c:if test="${not empty errorMessage}">
        <div class="alert alert-danger"><c:out value="${errorMessage}"/></div>
    </c:if>

    <div class="d-flex justify-content-between mb-3">
        <div>
            <a href="${pageContext.request.contextPath}/admin/usuarios/novo" class="btn btn-success">Novo Usuário</a>
            <a href="${pageContext.request.contextPath}/" class="btn btn-secondary ms-2">Voltar</a>
        </div>

        <form action="${pageContext.request.contextPath}/admin/usuarios" method="get" class="d-flex"
              style="width: 400px;">
            <input type="text" name="q" class="form-control me-2" placeholder="Pesquisar por usuário..."
                   value="${keyword}">
            <button type="submit" class="btn btn-primary">Pesquisar</button>
            <c:if test="${not empty keyword}">
                <a href="${pageContext.request.contextPath}/admin/usuarios" class="btn btn-outline-secondary ms-2">Limpar</a>
            </c:if>
        </form>
    </div>

    <table class="table table-striped align-middle" id="users-table">
        <thead>
        <tr>
            <th>ID</th>
            <th>Nome</th>
            <th>Email</th>
            <th>Telefone</th>
            <th>Cargo</th>
            <th>Unidades Atuais</th>
            <th>Vincular Nova Unidade</th>
            <th>Ações</th>
        </tr>
        </thead>
        <tbody>
        <c:choose>
            <c:when test="${empty users}">
                <tr>
                    <td colspan="7" class="text-center text-muted">Nenhum outro usuário encontrado.</td>
                </tr>
            </c:when>
            <c:otherwise>
                <c:forEach var="user" items="${users}">
                    <tr>
                        <td><c:out value="${user.id}"/></td>
                        <td><c:out value="${user.name}"/></td>
                        <td><c:out value="${user.email}"/></td>
                        <td><c:out value="${not empty user.phone ? user.phone : 'Não informado'}"/></td>
                        <td><span class="badge bg-secondary"><c:out value="${user.role}"/></span></td>

                        <td>
                            <c:choose>
                                <c:when test="${user.role == 'ADMIN'}">
                                    <span class="badge bg-light text-dark border">Acesso Global</span>
                                </c:when>
                                <c:when test="${empty user.units}">
                                    <span class="text-muted small">Nenhuma unidade</span>
                                </c:when>
                                <c:otherwise>
                                    <ul class="list-unstyled mb-0 small">
                                        <c:forEach var="userUnit" items="${user.units}">
                                            <li class="d-flex justify-content-between align-items-center mb-1">
                                                <span>&#8226; <c:out value="${userUnit.identifier}"/></span>
                                                <form action="${pageContext.request.contextPath}/admin/usuarios/${user.id}/desvincular-unidade"
                                                      method="post" style="display:inline;">
                                                    <input type="hidden" name="unitId" value="${userUnit.id}">
                                                    <button type="submit"
                                                            class="btn btn-sm btn-link text-danger p-0 ms-2"
                                                            title="Desvincular" style="text-decoration:none;">&times;
                                                    </button>
                                                </form>
                                            </li>
                                        </c:forEach>
                                    </ul>
                                </c:otherwise>
                            </c:choose>
                        </td>

                        <td>
                            <c:choose>
                                <c:when test="${user.role == 'ADMIN'}">
                                    <span class="text-muted small">Não aplicável</span>
                                </c:when>
                                <c:otherwise>
                                    <form action="${pageContext.request.contextPath}/admin/usuarios/${user.id}/vincular-unidade"
                                          method="post" class="d-flex flex-column gap-2"
                                          onsubmit="const unit = this.querySelector('.unit-select'); if(unit.disabled || unit.value === '') { alert('Selecione a unidade até o final antes de vincular.'); return false; }">
                                        <div class="d-flex gap-2">
                                            <select class="form-select form-select-sm block-select"
                                                    data-userid="${user.id}" style="width: 100px;">
                                                <option value="" disabled selected>Bloco...</option>
                                                <c:forEach var="block" items="${blocks}">
                                                    <option value="${block.id}" data-floors="${block.totalFloors}">
                                                        <c:out value="${block.identifier}"/></option>
                                                </c:forEach>
                                            </select>
                                            <select class="form-select form-select-sm floor-select"
                                                    id="floor-select-${user.id}" data-userid="${user.id}"
                                                    style="width: 100px;" disabled>
                                                <option value="" disabled selected>Andar...</option>
                                            </select>
                                        </div>
                                        <div class="d-flex gap-2">
                                            <select name="unitId" class="form-select form-select-sm unit-select"
                                                    id="unit-select-${user.id}" disabled required>
                                                <option value="" disabled selected>Unidade...</option>
                                            </select>
                                            <button type="submit" class="btn btn-sm btn-outline-primary">Vincular
                                            </button>
                                        </div>
                                    </form>
                                </c:otherwise>
                            </c:choose>
                        </td>

                        <td>
                            <div class="d-flex gap-2">
                                <a href="${pageContext.request.contextPath}/admin/usuarios/${user.id}/editar"
                                   class="btn btn-sm btn-warning">Editar</a>
                                <c:if test="${user.id != currentUser.id}">
                                    <form action="${pageContext.request.contextPath}/admin/usuarios/${user.id}/excluir"
                                          method="post"
                                          onsubmit="return confirm('Tem certeza que deseja excluir este usuário?');">
                                        <button type="submit" class="btn btn-sm btn-danger">Excluir</button>
                                    </form>
                                </c:if>
                            </div>
                        </td>
                    </tr>
                </c:forEach>
            </c:otherwise>
        </c:choose>
        </tbody>
    </table>
</div>

<script>
    // Script que consulta a api para buscar as unidades disponíveis com base no bloco e andar selecionados
    document.addEventListener("DOMContentLoaded", function () {
        // Lógica de seleção de bloco
        document.querySelectorAll('.block-select').forEach(select => {
            select.addEventListener('change', function () {
                const userId = this.getAttribute('data-userid');
                const floorSelect = document.getElementById('floor-select-' + userId);
                const unitSelect = document.getElementById('unit-select-' + userId);
                const selectedOption = this.options[this.selectedIndex];
                const totalFloors = parseInt(selectedOption.getAttribute('data-floors'));

                floorSelect.innerHTML = '<option value="" disabled selected>Andar...</option>';
                for (let i = 1; i <= totalFloors; i++) {
                    floorSelect.innerHTML += `<option value="` + i + `">` + i + `º Andar</option>`;
                }
                floorSelect.disabled = false;

                unitSelect.innerHTML = '<option value="" disabled selected>Unidade...</option>';
                unitSelect.disabled = true;
            });
        });

        // Lógica de seleção de andar
        document.querySelectorAll('.floor-select').forEach(select => {
            select.addEventListener('change', function () {
                const userId = this.getAttribute('data-userid');
                const blockSelect = document.querySelector('.block-select[data-userid="' + userId + '"]');
                const unitSelect = document.getElementById('unit-select-' + userId);

                const blocoId = blockSelect.value;
                const andar = this.value;

                unitSelect.innerHTML = '<option value="" disabled selected>Buscando...</option>';
                unitSelect.disabled = true;

                const url = "${pageContext.request.contextPath}/api/unidades/buscar?blocoId=" + blocoId + "&andar=" + andar;

                fetch(url)
                    .then(response => {
                        if (!response.ok) {
                            throw new Error("Erro HTTP: " + response.status);
                        }
                        return response.json();
                    })
                    .then(data => {
                        unitSelect.innerHTML = '<option value="" disabled selected>Unidade...</option>';

                        if (data.length === 0) {
                            unitSelect.innerHTML = '<option value="" disabled selected>Nenhuma encontrada</option>';
                        } else {
                            data.forEach(unit => {
                                // Corta a string pelos traços e pega apenas o último pedaço
                                const numeroUnidade = unit.identifier.split('-').pop();

                                // Insere apenas o número limpo no select
                                unitSelect.innerHTML += '<option value="' + unit.id + '">' + numeroUnidade + '</option>';
                            });
                            unitSelect.disabled = false;
                        }
                    })
                    .catch(error => {
                        console.error('Falha no Fetch:', error);
                        unitSelect.innerHTML = '<option value="" disabled selected>Erro de conexão</option>';
                    });
            });
        });
    });
</script>
</body>
</html>