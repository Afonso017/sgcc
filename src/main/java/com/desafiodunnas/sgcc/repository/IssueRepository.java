package com.desafiodunnas.sgcc.repository;

import com.desafiodunnas.sgcc.domain.Issue;
import com.desafiodunnas.sgcc.domain.Unit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

/**
 * Repositório responsável pelas operações de persistência e consultas
 * no banco de dados para a entidade de chamados.
 */
@Repository
public interface IssueRepository extends JpaRepository<Issue, Long> {

    /**
     * Realiza uma varredura global e abrangente em múltiplos campos do chamado.
     * Pesquisa por correspondências no título, descrição, tipo, status, identificador da unidade,
     * nome do autor ou data de criação.
     *
     * @param keyword O termo de pesquisa fornecido pelo usuário.
     * @return Lista integral de chamados que contenham o termo em qualquer um dos campos indexados.
     */
    @Query("SELECT i FROM Issue i WHERE " +
            "LOWER(i.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(i.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(i.type.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(i.status.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(i.unit.identifier) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(i.createdBy.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(CAST(i.createdAt AS string)) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Issue> searchComprehensiveGlobal(@Param("keyword") String keyword);

    /**
     * Realiza uma varredura abrangente em múltiplos campos, mas restrita
     * ao escopo de unidades do morador. Garante que nenhuma informação de outras unidades
     * seja vazada durante a pesquisa cruzada.
     *
     * @param units   A lista de unidades autorizadas para este morador.
     * @param keyword O termo de pesquisa fornecido pelo usuário.
     * @return Lista de chamados pertencentes às unidades do morador que correspondam ao termo de pesquisa.
     */
    @Query("SELECT i FROM Issue i WHERE i.unit IN :units AND (" +
            "LOWER(i.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(i.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(i.type.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(i.status.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(i.unit.identifier) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(i.createdBy.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(CAST(i.createdAt AS string)) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Issue> searchComprehensiveForResident(@Param("units") Set<Unit> units, @Param("keyword") String keyword);
}