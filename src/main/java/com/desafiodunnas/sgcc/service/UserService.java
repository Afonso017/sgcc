package com.desafiodunnas.sgcc.service;

import com.desafiodunnas.sgcc.domain.Unit;
import com.desafiodunnas.sgcc.domain.User;
import com.desafiodunnas.sgcc.repository.UnitRepository;
import com.desafiodunnas.sgcc.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    /**
     * Registra um novo usuário no sistema.
     * Garante a unicidade do email (usado como login) e aplica o hash BCrypt na senha
     * antes da persistência.
     */
    @Transactional
    public void createUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("O email já está em uso.");
        }

        try {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userRepository.save(user);
        } catch (Exception e) {
            System.err.println("Erro ao salvar o usuário no banco de dados");
            e.printStackTrace(System.err);
            throw new RuntimeException("Falha na criação de usuário", e);
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
    public void updateUser(Long id, User updatedUser) {
        try {
            User existing = userRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

            existing.setName(updatedUser.getName());
            existing.setEmail(updatedUser.getEmail());
            existing.setRole(updatedUser.getRole());
            existing.setPhone(updatedUser.getPhone());

            if (updatedUser.getPassword() != null && !updatedUser.getPassword().trim().isEmpty()) {
                existing.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
            }

            userRepository.save(existing);
        } catch (Exception e) {
            System.err.println("Erro ao atualizar o usuário");
            e.printStackTrace(System.err);
            throw new RuntimeException("Falha na atualização do usuário", e);
        }
    }

    @Transactional
    public void deleteUser(Long userId, User loggedInUser) {
        try {
            if (userId.equals(loggedInUser.getId())) {
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