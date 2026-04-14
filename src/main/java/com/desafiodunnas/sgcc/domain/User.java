package com.desafiodunnas.sgcc.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

/**
 * Entidade que representa os usuários do sistema.
 * Pode ser um Administrador, Colaborador ou Morador, definido pelo atributo 'role'.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(length = 20)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    /**
     * Relacionamento N:N que define quais unidades habitacionais pertencem ou
     * estão sob a responsabilidade deste usuário.
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_units",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "unit_id")
    )
    @Builder.Default
    private Set<Unit> units = new HashSet<>();
}