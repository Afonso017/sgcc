package com.desafiodunnas.sgcc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String login() {
        try {
            return "login";
        } catch (Exception e) {
            System.err.println("Erro ao acessar a pagina de login");
            e.printStackTrace(System.err);
            return "error";
        }
    }
}