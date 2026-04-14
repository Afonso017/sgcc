package com.desafiodunnas.sgcc.repository;

import com.desafiodunnas.sgcc.domain.Issue;
import com.desafiodunnas.sgcc.domain.Unit;
import com.desafiodunnas.sgcc.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

/**
 * Repositório responsável pelas operações de persistência e consultas
 * no banco de dados para a entidade de chamados.
 */
@Repository
public interface IssueRepository extends JpaRepository<Issue, Long> {

    /**
     * Realiza uma busca de chamados aplicando a regra de negócio
     * de visibilidade do morador. <br/>
     * Um morador só tem permissão para acessar chamados sob duas condições: <br/>
     * 1. Ele mesmo é o autor do chamado. <br/>
     * 2. O chamado pertence a uma unidade habitacional à qual ele está vinculado. <br/>
     * @param user O usuário (morador) atual realizando a requisição.
     * @param units Coleção de unidades vinculadas ao perfil do morador.
     * @param keyword Palavra-chave para filtro parcial (LIKE) na descrição do problema.
     * @return Lista de chamados correspondentes aos critérios de segurança e pesquisa.
     */
    @Query(
            "SELECT i " +
            "FROM Issue i " +
            "WHERE (i.createdBy = :user OR i.unit IN :units) " +
            "AND LOWER(i.description) LIKE LOWER(CONCAT('%', :keyword, '%'))"
    )
    List<Issue> searchByDescriptionForResident(@Param("user") User user,
                                               @Param("units") Collection<Unit> units,
                                               @Param("keyword") String keyword
    );

    /**
     * Realiza uma busca global de chamados no condomínio inteiro.
     * Esta consulta ignora restrições de unidades e é destinada exclusivamente
     * a usuários com privilégios operacionais ou administrativos (ADMIN, COLABORADOR).
     * @param keyword Palavra-chave para filtro parcial (LIKE) na descrição do problema.
     * @return Lista integral de chamados que contenham a palavra-chave pesquisada.
     */

    @Query(
            "SELECT i " +
            "FROM Issue i " +
            "WHERE LOWER(i.description) LIKE LOWER(CONCAT('%', :keyword, '%'))"
    )
    List<Issue> searchByDescriptionGlobal(@Param("keyword") String keyword);
}