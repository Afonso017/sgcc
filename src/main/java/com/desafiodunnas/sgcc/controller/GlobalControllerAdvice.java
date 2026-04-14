package com.desafiodunnas.sgcc.controller;

import com.desafiodunnas.sgcc.domain.User;
import com.desafiodunnas.sgcc.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Objects;

/**
 * Esta classe fornece um tratamento global para os controladores.
 * Ela inclui uma função que obtém o usuário atualmente autenticado e um manipulador de exceção que lida com erros de upload de arquivos.
 */
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalControllerAdvice {

    private final UserRepository userRepository;

    @ModelAttribute("currentUser")
    public User getCurrentUser(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated() && !Objects.equals(authentication.getPrincipal(), "anonymousUser")) {
            return userRepository.findByEmail(authentication.getName()).orElse(null);
        }
        return null;
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleMaxSizeException(HttpServletRequest request, RedirectAttributes redirectAttributes) {
        System.err.println("Tentativa de upload excedeu o tamanho máximo configurado.");

        redirectAttributes.addFlashAttribute("errorMessage", "Erro: O tamanho total dos anexos excede o limite permitido (15MB). Tente enviar arquivos menores.");

        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/issues/nova");
    }
}