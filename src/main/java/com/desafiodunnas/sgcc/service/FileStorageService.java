package com.desafiodunnas.sgcc.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/**
 * Serviço de infraestrutura dedicado ao upload/download de arquivos.
 * Isola a camada de sistema de arquivos do restante da lógica de negócio.
 */
@Service
public class FileStorageService {

    private final Path rootLocation = Paths.get("uploads");

    public FileStorageService() {
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("Não foi possível inicializar a pasta de uploads", e);
        }
    }

    /**
     * Recebe um arquivo binário e o salva no disco físico ou volume Docker.
     * Utiliza UUID para evitar colisão de nomes e ataques de substituição de arquivos.
     * @param file O arquivo enviado pelo formulário.
     * @return O caminho relativo gerado para o arquivo salvo.
     */
    public String store(MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return null;
            }

            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null && originalFilename.contains(".")
                    ? originalFilename.substring(originalFilename.lastIndexOf("."))
                    : "";

            String newFilename = UUID.randomUUID() + extension;

            Path destinationFile = this.rootLocation.resolve(Paths.get(newFilename)).normalize().toAbsolutePath();

            Files.copy(file.getInputStream(), destinationFile, StandardCopyOption.REPLACE_EXISTING);

            return "/uploads/" + newFilename;

        } catch (Exception e) {
            System.err.println("Erro ao salvar o arquivo");
            e.printStackTrace(System.err);
            throw new RuntimeException("Falha ao armazenar o arquivo", e);
        }
    }
}