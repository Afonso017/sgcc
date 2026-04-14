<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <title>Erro no Sistema</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<div class="container mt-5">
    <div class="alert alert-danger" role="alert">
        <h4 class="alert-heading">Ops! Ocorreu um problema.</h4>
        <p><c:out value="${errorMessage != null ? errorMessage : 'Ocorreu um erro interno no servidor.'}" /></p>
        <hr>
        <a href="/" class="btn btn-primary">Voltar para a Home</a>
    </div>
</div>
</body>
</html>