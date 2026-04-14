package com.desafiodunnas.sgcc.security;

import jakarta.servlet.DispatcherType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuração central de segurança da aplicação utilizando Spring Security.
 * Define as regras de controle de acesso, proteção de rotas
 * e o comportamento das páginas de login e logout.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Configura a cadeia de filtros de segurança. <br/>
     * Regras estabelecidas: <br/>
     * - Arquivos estáticos (CSS/JS) possuem acesso público. <br/>
     * - Áreas sensíveis de administração (/blocos e /usuarios) são restritas
     * exclusivamente a usuários com o cargo de ADMIN. <br/>
     * - As demais rotas do sistema exigem usuário autenticado.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .dispatcherTypeMatchers(DispatcherType.FORWARD, DispatcherType.ERROR).permitAll()
                        .requestMatchers("/css/**", "/js/**").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                );
        return http.build();
    }

    /**
     * Define o algoritmo de hash BCrypt como o padrão de criptografia do sistema.
     * Garante que as senhas jamais sejam armazenadas em texto plano no banco de dados.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}