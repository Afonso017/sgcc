package com.desafiodunnas.sgcc;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class SgccApplication {

	public static void main(String[] args) {
		try {
			SpringApplication.run(SgccApplication.class, args);
		} catch (Exception e) {
			System.err.println("Erro crítico ao iniciar a aplicação Spring Boot");
			e.printStackTrace(System.err);
		}
	}

	@PostConstruct
	public void init() {
		try {
			// Utiliza o horário de Brasília
			TimeZone.setDefault(TimeZone.getTimeZone("America/Sao_Paulo"));
		} catch (Exception e) {
			System.err.println("Erro ao configurar o fuso horário da aplicação");
			e.printStackTrace(System.err);
		}
	}
}