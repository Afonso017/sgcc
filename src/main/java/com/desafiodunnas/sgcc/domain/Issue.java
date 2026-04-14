package com.desafiodunnas.sgcc.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidade central do sistema. Representa um chamado ou ocorrência referente a uma unidade do condomínio.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "issues")
public class Issue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_id", nullable = false)
    private Unit unit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id", nullable = false)
    private IssueType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id", nullable = false)
    private IssueStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "finished_at")
    private LocalDateTime finishedAt;

    @OneToMany(mappedBy = "issue", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Attachment> attachments = new ArrayList<>();

    @OneToMany(mappedBy = "issue", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Comment> comments = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public String getFormattedCreatedAt() {
        if (this.createdAt == null) return "";
        return this.createdAt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm"));
    }

    public String getFormattedFinishedAt() {
        if (this.finishedAt == null) return "";
        return this.finishedAt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm"));
    }

    /**
     * Calcula a data e hora exata em que o chamado expirará,
     * baseando-se nas horas de SLA configuradas no tipo de chamado.
     */
    public LocalDateTime getDeadline() {
        if (this.createdAt == null || this.type == null) return null;
        return this.createdAt.plusHours(this.type.getSlaHours());
    }

    public String getFormattedDeadline() {
        LocalDateTime deadline = getDeadline();
        if (deadline == null) return "Não definido";
        return deadline.format(DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm"));
    }

    /**
     * Gera dinamicamente o código HTML correspondente
     * ao status atual de resolução do SLA do chamado.
     */
    public String getSlaStatusBadge() {
        LocalDateTime deadline = getDeadline();
        if (deadline == null) return "";

        // Se o chamado já foi encerrado, compara a data de conclusão com o prazo
        if (this.finishedAt != null) {
            if (this.finishedAt.isBefore(deadline) || this.finishedAt.isEqual(deadline)) {
                return "<span class='badge bg-success'>Resolvido no Prazo</span>";
            } else {
                return "<span class='badge bg-danger'>Resolvido com Atraso</span>";
            }
        }

        // Se continua em aberto, verifica se a hora atual já ultrapassou o limite
        if (LocalDateTime.now().isAfter(deadline)) {
            return "<span class='badge bg-danger'>Atrasado</span>";
        } else {
            return "<span class='badge bg-info text-dark'>No Prazo</span>";
        }
    }
}