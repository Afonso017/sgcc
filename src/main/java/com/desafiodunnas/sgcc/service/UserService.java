package com.desafiodunnas.sgcc.service;

import com.desafiodunnas.sgcc.domain.Unit;
import com.desafiodunnas.sgcc.domain.User;
import com.desafiodunnas.sgcc.repository.UnitRepository;
import com.desafiodunnas.sgcc.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * Serviço responsável por encapsular as regras de negócio de gerenciamento de usuários,
 * criptografia de credenciais e controle de vínculos habitacionais.
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UnitRepository unitRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userDetailsService;

    /**
     * Registra um novo usuário no sistema.
     * Valida campos obrigatórios, garante a unicidade do email (usado como login)
     * e aplica o hash BCrypt na senha antes da persistência.
     */
    @Transactional
    public void createUser(User user) {
        try {
            // Validações de campos obrigatórios
            if (user.getName() == null || user.getName().trim().isEmpty()) {
                throw new IllegalArgumentException("O nome é obrigatório.");
            }

            if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
                throw new IllegalArgumentException("O email é obrigatório.");
            }

            if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
                throw new IllegalArgumentException("A senha é obrigatória.");
            }

            if (user.getRole() == null) {
                throw new IllegalArgumentException("O nível de acesso (cargo) é obrigatório.");
            }

            if (user.getPhone() != null && user.getPhone().length() > 20) {
                throw new IllegalArgumentException("O número de telefone não pode exceder 20 caracteres.");
            }

            if (userRepository.existsByEmail(user.getEmail())) {
                throw new IllegalArgumentException("O email já está em uso.");
            }

            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userRepository.save(user);

        } catch (Exception e) {
            System.err.println("Erro ao salvar o usuário no banco de dados");
            e.printStackTrace(System.err);
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public User findUserById(Long id) {
        try {
            return userRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
        } catch (Exception e) {
            System.err.println("Erro ao buscar o usuário pelo ID");
            e.printStackTrace(System.err);
            throw new RuntimeException("Falha na busca do usuário", e);
        }
    }

    /**
     * Atualiza os dados de um usuário existente.
     * A senha só é recriptografada e atualizada se o administrador fornecer uma nova
     * no formulário de edição.
     */
    @Transactional
    public void updateUser(User updatedUser) {
        try {
            User existingUser = userRepository.findById(updatedUser.getId())
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

            // Identifica quem está logado para aplicar as regras de segurança na edição de perfil
            String loggedInEmail = Objects.requireNonNull(
                    SecurityContextHolder.getContext().getAuthentication()
            ).getName();

            // Guarda se a requisição atual é do próprio usuário editando o próprio perfil
            boolean isEditingSelf = existingUser.getEmail().equals(loggedInEmail);

            // Atualiza campos básicos
            if (updatedUser.getName() != null) {
                if (updatedUser.getName().trim().isEmpty()) {
                    throw new IllegalArgumentException("O nome não pode ser vazio.");
                }
                existingUser.setName(updatedUser.getName());
            }

            if (updatedUser.getEmail() != null) {
                if (updatedUser.getEmail().trim().isEmpty()) {
                    throw new IllegalArgumentException("O email não pode ser vazio.");
                }
                existingUser.setEmail(updatedUser.getEmail());
            }

            if (updatedUser.getPhone() != null) {
                if (updatedUser.getPhone().length() > 20) {
                    throw new IllegalArgumentException("O número de telefone não pode exceder 20 caracteres.");
                }
                existingUser.setPhone(updatedUser.getPhone());
            }

            // Verifica se está mudando o próprio cargo
            if (updatedUser.getRole() != null && !existingUser.getRole().equals(updatedUser.getRole())) {
                if (isEditingSelf) {
                    throw new IllegalStateException("Ação negada: Você não pode alterar seu próprio nível de acesso " +
                            "por questões de segurança.");
                }
                existingUser.setRole(updatedUser.getRole());
            }

            // Atualiza a senha apenas se uma nova foi digitada
            if (updatedUser.getPassword() != null && !updatedUser.getPassword().trim().isEmpty()) {
                existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
            }

            // Salva as alterações no banco de dados
            userRepository.save(existingUser);

            // Atualiza a sessão do Spring com os novos dados alterados
            if (isEditingSelf) {
                UserDetails newDetails = userDetailsService.loadUserByUsername(existingUser.getEmail());
                Authentication newAuth = new UsernamePasswordAuthenticationToken(
                        newDetails,
                        SecurityContextHolder.getContext().getAuthentication().getCredentials(),
                        newDetails.getAuthorities()
                );
                SecurityContextHolder.getContext().setAuthentication(newAuth);
            }
        } catch (Exception e) {
            System.err.println("Erro ao atualizar o usuário");
            e.printStackTrace(System.err);
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Transactional
    public void deleteUser(Long userId) {
        try {
            User userToDelete = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

            if (userToDelete.getId().equals(1L)) {
                throw new IllegalStateException("Ação negada: O administrador principal do sistema não pode ser excluído.");
            }

            // Bloqueia a auto-exclusão da conta
            String loggedInEmail = Objects.requireNonNull(
                    SecurityContextHolder.getContext().getAuthentication()
            ).getName();

            if (userToDelete.getEmail().equals(loggedInEmail)) {
                throw new IllegalStateException("Ação negada: Você não pode excluir sua própria conta.");
            }

            userRepository.deleteById(userId);
        } catch (Exception e) {
            System.err.println("Erro ao excluir o usuário");
            e.printStackTrace(System.err);
            throw new RuntimeException("Não foi possível excluir. " +
                    "O usuário pode ter chamados ou comentários vinculados a ele.", e);
        }
    }

    /**
     * Estabelece o vínculo relacional (N:N) indicando que uma unidade habitacional
     * pertence ou está sob responsabilidade do usuário.
     */
    @Transactional
    public void linkUserToUnit(Long userId, Long unitId) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
            Unit unit = unitRepository.findById(unitId)
                    .orElseThrow(() -> new IllegalArgumentException("Unidade não encontrada"));

            user.getUnits().add(unit);
            userRepository.save(user);
        } catch (Exception e) {
            System.err.println("Erro ao vincular usuário à unidade");
            e.printStackTrace(System.err);
            throw new RuntimeException("Falha na vinculação de unidade", e);
        }
    }

    /**
     * Remove o vínculo relacional entre um usuário e uma unidade.
     */
    @Transactional
    public void unlinkUserFromUnit(Long userId, Long unitId) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
            Unit unit = unitRepository.findById(unitId)
                    .orElseThrow(() -> new IllegalArgumentException("Unidade não encontrada"));

            user.getUnits().remove(unit);
            userRepository.save(user);
        } catch (Exception e) {
            System.err.println("Erro ao remover vínculo do usuário com a unidade");
            e.printStackTrace(System.err);
            throw new RuntimeException("Falha ao remover a unidade do usuário", e);
        }
    }

    /**
     * Realiza uma busca flexível na base de usuários.
     * Permite que o administrador encontre um registro buscando tanto por
     * partes do nome quanto por partes do endereço de email.
     * @param keyword O termo de busca.
     * @return Lista de usuários que correspondem ao critério, ou todos caso a busca seja vazia.
     */
    public List<User> searchUsers(String keyword) {
        try {
            if (keyword == null || keyword.trim().isEmpty()) {
                return userRepository.findAll();
            }
            String search = keyword.trim();
            return userRepository.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(search, search);
        } catch (Exception e) {
            System.err.println("Erro ao pesquisar usuários");
            e.printStackTrace(System.err);
            throw new RuntimeException("Falha na pesquisa de usuários", e);
        }
    }
}