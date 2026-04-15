-- Adiciona a nova coluna para suportar anexos em comentários
ALTER TABLE attachments
    ADD COLUMN comment_id BIGINT;

-- Adiciona a restrição de chave estrangeira
ALTER TABLE attachments
    ADD CONSTRAINT fk_attachments_comments
        FOREIGN KEY (comment_id)
            REFERENCES comments (id);

-- Remove a obrigatoriedade da coluna issue_id, permitindo que anexos possam estar associados apenas a comentários
ALTER TABLE attachments ALTER COLUMN issue_id DROP NOT NULL;