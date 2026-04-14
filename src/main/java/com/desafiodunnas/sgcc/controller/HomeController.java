package com.desafiodunnas.sgcc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller para a página inicial do sistema.
 */
@Controller
public class HomeController {

    @GetMapping("/")
    public String index() {
        try {
            return "index";
        } catch (Exception e) {
            System.err.println("Erro ao carregar a pagina inicial");
            e.printStackTrace(System.err);
            return "error";
        }
    }
}