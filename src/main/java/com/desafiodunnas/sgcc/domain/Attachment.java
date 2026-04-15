package com.desafiodunnas.sgcc.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidade que armazena a referência física (URL ou caminho do disco)
 * de fotos ou documentos anexados durante a abertura, ou comentário de um chamado.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "attachments")
public class Attachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "issue_id")
    private Issue issue;

    @ManyToOne
    @JoinColumn(name = "comment_id")
    private Comment comment;

    /**
     * Caminho relativo mapeado pelo WebConfig para localizar o arquivo na pasta de uploads do servidor.
     */
    @Column(name = "file_url", nullable = false)
    private String fileUrl;
}