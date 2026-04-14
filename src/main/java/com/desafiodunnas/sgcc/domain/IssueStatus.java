package com.desafiodunnas.sgcc.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidade paramétrica que rege os status possíveis de um chamado.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "issue_statuses")
public class IssueStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    /**
     * Indica se este é o status atribuído automaticamente quando um novo chamado é criado.
     * Deve haver apenas um status padrão no banco de dados.
     */
    @Column(name = "is_default", nullable = false)
    private Boolean isDefault;

    /**
     * Indica se este status encerra o chamado (Ex: "Concluído", "Cancelado").
     * Ao atingir um status final, o relógio do SLA é paralisado.
     */
    @Column(name = "is_final", nullable = false)
    private Boolean isFinal;
}