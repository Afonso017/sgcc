package com.desafiodunnas.sgcc.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidade paramétrica que define as categorias de chamados disponíveis no sistema e o tempo esperado de resolução.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "issue_types")
public class IssueType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    /**
     * Service Level Agreement (SLA).
     * Define, em horas, o prazo máximo para um chamado deste tipo ser concluído.
     */
    @Column(name = "sla_hours", nullable = false)
    private Integer slaHours;
}