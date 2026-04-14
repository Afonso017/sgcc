package com.desafiodunnas.sgcc.component;

import com.desafiodunnas.sgcc.domain.IssueStatus;
import com.desafiodunnas.sgcc.domain.IssueType;
import com.desafiodunnas.sgcc.domain.User;
import com.desafiodunnas.sgcc.domain.UserRole;
import com.desafiodunnas.sgcc.repository.IssueStatusRepository;
import com.desafiodunnas.sgcc.repository.IssueTypeRepository;
import com.desafiodunnas.sgcc.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Classe responsável por popular o banco de dados com um usuário admin e alguns status e tipos de chamados iniciais.
 */
@Component
@RequiredArgsConstructor
public class AdminSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final IssueStatusRepository issueStatusRepository;
    private final IssueTypeRepository issueTypeRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String @NonNull ... args) {
        try {
            if (userRepository.findByEmail("admin@gmail.com").isEmpty()) {
                User admin = User.builder()
                        .name("Administrador do Sistema")
                        .email("admin@gmail.com")
                        .password(passwordEncoder.encode("admin123"))
                        .role(UserRole.ADMIN)
                        .build();
                userRepository.save(admin);
                System.out.println("Usuário admin criado com sucesso");
            }

            // Adiciona status iniciais e tipos de chamados se não existirem
            if (issueStatusRepository.count() == 0) {
                issueStatusRepository.save(IssueStatus.builder().name("Aberto").isDefault(true).isFinal(false).build());
                issueStatusRepository.save(IssueStatus.builder().name("Em Andamento").isDefault(false).isFinal(false).build());
                issueStatusRepository.save(IssueStatus.builder().name("Concluído").isDefault(false).isFinal(true).build());
            }

            if (issueTypeRepository.count() == 0) {
                issueTypeRepository.save(IssueType.builder().title("Manutenção").slaHours(48).build());
                issueTypeRepository.save(IssueType.builder().title("Reclamação").slaHours(24).build());
                issueTypeRepository.save(IssueType.builder().title("Dúvida").slaHours(12).build());
            }
        } catch (Exception e) {
            System.err.println("Erro ao executar o seeder inicial");
            e.printStackTrace(System.err);
        }
    }
}