package com.desafiodunnas.sgcc.security;

import com.desafiodunnas.sgcc.domain.User;
import com.desafiodunnas.sgcc.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Serviço responsável por atuar como uma ponte entre o banco de dados da aplicação
 * e o mecanismo de autenticação interno do Spring Security.
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Busca um usuário no banco de dados com base no email fornecido na tela de login
     * e o converte para o contrato exigido pelo Spring Security (UserDetails).
     *
     * @param email O email digitado pelo usuário na tentativa de login.
     * @return Um objeto UserDetails contendo as credenciais criptografadas e o cargo.
     * @throws UsernameNotFoundException Se o email não constar na base de dados.
     */
    @Override
    public UserDetails loadUserByUsername(@NonNull String email) throws UsernameNotFoundException {
        try {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

            return org.springframework.security.core.userdetails.User
                    .withUsername(user.getEmail())
                    .password(user.getPassword())
                    .roles(user.getRole().name())
                    .build();
        } catch (Exception e) {
            System.err.println("Erro ao carregar usuário para autenticação");
            e.printStackTrace(System.err);
            throw new UsernameNotFoundException("Falha na autenticação", e);
        }
    }
}