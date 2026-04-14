<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <title>Novo Chamado</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<div class="container mt-5">
    <h2>Abrir Novo Chamado</h2>

    <c:if test="${not empty errorMessage}">
        <div class="alert alert-danger"><c:out value="${errorMessage}"/></div>
    </c:if>

    <form action="${pageContext.request.contextPath}/issues" method="post" enctype="multipart/form-data">

        <c:choose>
            <c:when test="${currentUser.role != 'ADMIN'}">
                <div class="mb-3">
                    <label class="form-label">Selecione a unidade:</label>
                    <select name="unitId" class="form-select" required>
                        <option value="" disabled selected>Selecione a unidade...</option>
                        <c:forEach var="unit" items="${userUnits}">
                            <option value="${unit.id}"><c:out value="${unit.identifier}" /></option>
                        </c:forEach>
                    </select>
                    <c:if test="${empty userUnits}">
                        <small class="text-danger">Você não possui unidades vinculadas. Contate a administração para poder abrir chamados.</small>
                    </c:if>
                </div>
            </c:when>
            <c:otherwise>
                <div class="row mb-3">
                    <div class="col-md-4">
                        <label class="form-label">Bloco:</label>
                        <select id="block-select" class="form-select" required>
                            <option value="" disabled selected>Escolha o bloco...</option>
                            <c:forEach var="block" items="${blocks}">
                                <option value="${block.id}" data-floors="${block.totalFloors}"><c:out value="${block.identifier}" /></option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="col-md-4">
                        <label class="form-label">Andar:</label>
                        <select id="floor-select" class="form-select" disabled required>
                            <option value="" disabled selected>Selecione o bloco primeiro</option>
                        </select>
                    </div>
                    <div class="col-md-4">
                        <label class="form-label">Unidade:</label>
                        <select name="unitId" id="unit-select" class="form-select" disabled required>
                            <option value="" disabled selected>Selecione o andar primeiro</option>
                        </select>
                    </div>
                </div>
            </c:otherwise>
        </c:choose>

        <div class="mb-3">
            <label class="form-label">Título do Chamado:
                <input type="text" name="title" class="form-control" required>
            </label>
        </div>

        <div class="mb-3">
            <label class="form-label">Tipo de Chamado:
                <select name="typeId" class="form-select" required>
                    <option value="" disabled selected>Qual a natureza da ocorrência?</option>
                    <c:forEach var="type" items="${types}">
                        <option value="${type.id}"><c:out value="${type.title}" /> (SLA: ${type.slaHours}h)</option>
                    </c:forEach>
                </select>
            </label>
        </div>

        <div class="mb-3">
            <label class="form-label">Descrição do Problema/Ocorrência:</label>
            <textarea name="description" class="form-control" rows="4" required placeholder="Descreva em detalhes o motivo do chamado..."></textarea>
        </div>

        <div class="mb-4">
            <label class="form-label">Anexar Fotos ou Documentos (Opcional):</label>
            <input type="file" name="files" class="form-control" multiple accept="image/*,.pdf,.doc,.docx">
            <small class="text-muted">Você pode selecionar múltiplos arquivos simultaneamente.</small>
        </div>

        <button type="submit" class="btn btn-primary" id="btn-submit"
        ${(currentUser.role != 'ADMIN' && empty userUnits) || (currentUser.role == 'ADMIN' && empty blocks) ? 'disabled' : ''}>
            Registrar Chamado
        </button>
        <a href="${pageContext.request.contextPath}/issues" class="btn btn-secondary">Cancelar</a>
    </form>
</div>

<c:if test="${currentUser.role == 'ADMIN'}">
    <script>
        document.addEventListener("DOMContentLoaded", function() {
            const blockSelect = document.getElementById('block-select');
            const floorSelect = document.getElementById('floor-select');
            const unitSelect = document.getElementById('unit-select');

            blockSelect.addEventListener('change', function() {
                const selectedOption = this.options[this.selectedIndex];
                const totalFloors = parseInt(selectedOption.getAttribute('data-floors'));

                floorSelect.innerHTML = '<option value="" disabled selected>Andar...</option>';
                for(let i = 1; i <= totalFloors; i++) {
                    floorSelect.innerHTML += '<option value="' + i + '">' + i + 'º Andar</option>';
                }
                floorSelect.disabled = false;
                unitSelect.innerHTML = '<option value="" disabled selected>Aguardando andar...</option>';
                unitSelect.disabled = true;
            });

            floorSelect.addEventListener('change', function() {
                const blocoId = blockSelect.value;
                const andar = this.value;

                unitSelect.innerHTML = '<option value="" disabled selected>Buscando unidades...</option>';
                unitSelect.disabled = true;

                fetch('${pageContext.request.contextPath}/api/unidades/buscar?blocoId=' + blocoId + '&andar=' + andar)
                    .then(response => {
                        if(!response.ok) throw new Error("Erro HTTP: " + response.status);
                        return response.json();
                    })
                    .then(data => {
                        unitSelect.innerHTML = '<option value="" disabled selected>Unidade...</option>';
                        if (data.length === 0) {
                            unitSelect.innerHTML = '<option value="" disabled selected>Nenhuma encontrada</option>';
                        } else {
                            data.forEach(unit => {
                                const numeroUnidade = unit.identifier.split('-').pop();
                                unitSelect.innerHTML += '<option value="' + unit.id + '">' + numeroUnidade + '</option>';
                            });
                            unitSelect.disabled = false;
                        }
                    })
                    .catch(error => {
                        console.error('Erro:', error);
                        unitSelect.innerHTML = '<option value="" disabled selected>Erro ao carregar</option>';
                    });
            });
        });
    </script>
</c:if>
</body>
</html>