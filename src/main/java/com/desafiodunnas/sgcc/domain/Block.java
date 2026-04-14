package com.desafiodunnas.sgcc.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidade que representa a infraestrutura física macro do condomínio (prédios).
 * Gerencia o agrupamento estrutural das unidades habitacionais.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "blocks")
public class Block {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String identifier;

    @Column(name = "total_floors", nullable = false)
    private Integer totalFloors;

    @Column(name = "apartments_per_floor", nullable = false)
    private Integer apartmentsPerFloor;

    /**
     * Lista de unidades que compõem este bloco.
     * Operações em cascata garantem que, ao remover um bloco,
     * todas as suas unidades sejam removidas do sistema.
     */
    @OneToMany(mappedBy = "block", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Unit> units = new ArrayList<>();
}