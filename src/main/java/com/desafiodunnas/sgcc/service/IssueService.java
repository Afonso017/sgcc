package com.desafiodunnas.sgcc.service;

import com.desafiodunnas.sgcc.domain.*;
import com.desafiodunnas.sgcc.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Serviço central para a gestão de chamados.
 * Orquestra validações de segurança, manipulação de anexos de sistema de arquivos
 * e a progressão do ciclo de vida do chamado.
 */
@Service
@RequiredArgsConstructor
public class IssueService {

    private final IssueRepository issueRepository;
    private final IssueStatusRepository issueStatusRepository;
    private final IssueTypeRepository issueTypeRepository;
    private final UnitRepository unitRepository;
    private final CommentRepository commentRepository;
    private final FileStorageService fileStorageService;

    /**
     * Cria um novo chamado no sistema.
     * Aplica automaticamente o status inicial padrão e valida se o usuário
     * possui autorização para abrir chamado na unidade especificada.
     * Processa e armazena anexos, se fornecidos.
     */
    @Transactional
    public void createIssue(Issue issue, User currentUser, Long unitId, Long typeId, List<MultipartFile> files) {
        try {
            IssueStatus defaultStatus = issueStatusRepository.findByIsDefaultTrue()
                    .orElseThrow(() -> new IllegalStateException("Status padrão não configurado no sistema"));

            IssueType type = issueTypeRepository.findById(typeId)
                    .orElseThrow(() -> new IllegalArgumentException("Tipo de chamado não encontrado"));

            Unit unit = unitRepository.findById(unitId)
                    .orElseThrow(() -> new IllegalArgumentException("Unidade não encontrada"));

            if (currentUser.getRole() != UserRole.ADMIN && !currentUser.getUnits().contains(unit)) {
                throw new IllegalStateException("Você só pode abrir chamados para as unidades em que está vinculado.");
            }

            issue.setStatus(defaultStatus);
            issue.setType(type);
            issue.setUnit(unit);
            issue.setCreatedBy(currentUser);

            if (files != null && !files.isEmpty()) {
                for (MultipartFile file : files) {
                    if (!file.isEmpty()) {
                        String fileUrl = fileStorageService.store(file);
                        Attachment attachment = Attachment.builder()
                                .fileUrl(fileUrl)
                                .issue(issue)
                                .build();
                        issue.getAttachments().add(attachment);
                    }
                }
            }

            issueRepository.save(issue);
        } catch (Exception e) {
            System.err.println("Erro ao criar a issue");
            e.printStackTrace(System.err);
            throw new RuntimeException("Falha na criação da issue: " + e.getMessage(), e);
        }
    }

    /**
     * Atualiza o status de progressão do chamado.
     * Bloqueia moradores de alterarem status. Caso o novo status seja o final,
     * paralisa o relógio de SLA registrando a data e hora de conclusão, além de bloquear a mudança de status.
     */
    @Transactional
    public void updateIssueStatus(Long issueId, Long newStatusId, User actionUser) {
        try {
            if (actionUser.getRole() == UserRole.MORADOR) {
                throw new IllegalStateException("Moradores não têm permissão para alterar o status.");
            }

            Issue issue = issueRepository.findById(issueId)
                    .orElseThrow(() -> new IllegalArgumentException("Issue não encontrada"));

            // Proíbe a mudança, caso o status atual já for final
            if (issue.getStatus().getIsFinal()) {
                throw new IllegalStateException("Este chamado já foi encerrado e não pode ter seu status alterado. " +
                        "Caso o problema persista, abra um novo chamado.");
            }

            IssueStatus newStatus = issueStatusRepository.findById(newStatusId)
                    .orElseThrow(() -> new IllegalArgumentException("Status não encontrado"));

            issue.setStatus(newStatus);

            if (newStatus.getIsFinal()) {
                issue.setFinishedAt(LocalDateTime.now());
            } else {
                issue.setFinishedAt(null);
            }

            issueRepository.save(issue);
        } catch (Exception e) {
            System.err.println("Erro ao atualizar o status da issue");
            e.printStackTrace(System.err);
            throw new RuntimeException("Falha na atualização de status", e);
        }
    }

    /**
     * Adiciona um comentário a um chamado, garantindo que moradores apenas
     * interajam com chamados do seu escopo de autorização.
     */
    @Transactional
    public void addComment(Long issueId, User author, String content) {
        try {
            Issue issue = issueRepository.findById(issueId)
                    .orElseThrow(() -> new IllegalArgumentException("Issue não encontrada"));

            // Bloqueia novos comentários em chamados já encerrados
            if (issue.getStatus().getIsFinal()) {
                throw new IllegalStateException("Não é possível adicionar comentários. " +
                        "Este chamado já encontra-se encerrado.");
            }

            if (author.getRole() == UserRole.MORADOR) {
                boolean isMORADORIssue = issue.getCreatedBy().getId().equals(author.getId()) ||
                        author.getUnits().contains(issue.getUnit());
                if (!isMORADORIssue) {
                    throw new IllegalStateException("Morador só pode comentar em chamados próprios ou de suas unidades");
                }
            }

            Comment comment = Comment.builder()
                    .content(content)
                    .author(author)
                    .issue(issue)
                    .build();

            commentRepository.save(comment);
        } catch (Exception e) {
            System.err.println("Erro ao adicionar comentário");
            e.printStackTrace(System.err);
            throw new RuntimeException("Falha ao adicionar comentário", e);
        }
    }

    public List<Issue> findAllIssuesForUser(User user, String keyword) {
        try {
            String search = (keyword == null) ? "" : keyword.trim();

            if (user.getRole() == UserRole.MORADOR) {
                return issueRepository.searchByDescriptionForResident(user, user.getUnits(), search);
            }
            return issueRepository.searchByDescriptionGlobal(search);
        } catch (Exception e) {
            System.err.println("Erro ao buscar chamados filtrados");
            e.printStackTrace(System.err);
            throw new RuntimeException("Falha na busca de chamados", e);
        }
    }

    public List<IssueType> findAllTypes() {
        return issueTypeRepository.findAll();
    }

    public List<IssueStatus> findAllStatuses() {
        return issueStatusRepository.findAll();
    }
}