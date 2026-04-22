package com.desafiodunnas.sgcc.controller;

import com.desafiodunnas.sgcc.domain.Issue;
import com.desafiodunnas.sgcc.domain.User;
import com.desafiodunnas.sgcc.domain.UserRole;
import com.desafiodunnas.sgcc.service.BlockService;
import com.desafiodunnas.sgcc.service.IssueService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller principal responsável pelo gerenciamento de chamados (issues),
 * interações e acompanhamento de SLAs.
 */
@Controller
@RequestMapping("/issues")
@RequiredArgsConstructor
public class IssueController {

    private final IssueService issueService;
    private final BlockService blockService;

    /**
     * Lista os chamados visíveis para o usuário logado e aplica
     * a ordenação focada no prazo (SLA) remanescente.
     */
    @GetMapping
    public String listIssues(@RequestParam(value = "q", required = false) String keyword,
                             @ModelAttribute("currentUser") User currentUser, Model model) {
        try {
            List<Issue> issues = new ArrayList<>(issueService.findAllIssuesForUser(currentUser, keyword));

            // Lógica de priorização: ordena pelos prazos mais próximos do vencimento
            issues.sort((i1, i2) -> {
                if (i1.getDeadline() == null && i2.getDeadline() == null) return 0;
                if (i1.getDeadline() == null) return 1;
                if (i2.getDeadline() == null) return -1;
                return i1.getDeadline().compareTo(i2.getDeadline());
            });

            model.addAttribute("issues", issues);
            model.addAttribute("statuses", issueService.findAllStatuses());
            model.addAttribute("keyword", keyword);
            return "issues/list";
        } catch (Exception e) {
            System.err.println("Erro ao carregar a listagem de chamados");
            e.printStackTrace(System.err);
            model.addAttribute("errorMessage", "Ocorreu um erro ao carregar a lista de chamados.");
            return "error";
        }
    }

    /**
     * Prepara o formulário de abertura de chamados, adaptando a interface
     * conforme o nível de permissão do usuário.
     */
    @GetMapping("/nova")
    public String newIssueForm(@ModelAttribute("currentUser") User currentUser, Model model) {
        try {
            model.addAttribute("issue", new Issue());
            model.addAttribute("types", issueService.findAllTypes());

            if (currentUser.getRole() != UserRole.ADMIN) {
                model.addAttribute("userUnits", currentUser.getUnits());
            } else {
                model.addAttribute("blocks", blockService.findAllBlocks());
            }

            return "issues/form";
        } catch (Exception e) {
            System.err.println("Erro ao renderizar o formulário de issue");
            e.printStackTrace(System.err);
            model.addAttribute("errorMessage", "Ocorreu um erro ao abrir o formulário.");
            return "error";
        }
    }

    @PostMapping
    public String createIssue(@ModelAttribute Issue issue,
                              @RequestParam Long unitId,
                              @RequestParam Long typeId,
                              @RequestParam(value = "files", required = false) List<MultipartFile> files,
                              @ModelAttribute("currentUser") User currentUser,
                              Model model
    ) {
        try {
            issueService.createIssue(issue, currentUser, unitId, typeId, files);
            return "redirect:/issues";
        } catch (Exception e) {
            System.err.println("Erro ao salvar um novo chamado");
            e.printStackTrace(System.err);
            model.addAttribute("errorMessage",
                    e.getCause() != null ? e.getCause().getMessage() : "Erro ao salvar chamado.");
            return "error";
        }
    }

    @PostMapping("/{issueId}/status")
    public String updateStatus(@PathVariable Long issueId,
                               @RequestParam Long newStatusId,
                               @ModelAttribute("currentUser") User currentUser,
                               RedirectAttributes redirectAttributes
    ) {
        try {
            issueService.updateIssueStatus(issueId, newStatusId, currentUser);
            return "redirect:/issues";
        } catch (Exception e) {
            System.err.println("Erro ao atualizar o status");
            e.printStackTrace(System.err);
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Não foi possível atualizar o status: " + e.getMessage());
            return "redirect:/issues";
        }
    }

    @PostMapping("/{issueId}/comentarios")
    public String addComment(@PathVariable Long issueId,
                             @RequestParam String content,
                             @RequestParam(value = "files", required = false) List<MultipartFile> files,
                             @ModelAttribute("currentUser") User currentUser,
                             RedirectAttributes redirectAttributes
    ) {
        try {
            issueService.addComment(issueId, currentUser, content, files);
            return "redirect:/issues";
        } catch (Exception e) {
            System.err.println("Erro ao adicionar comentário");
            e.printStackTrace(System.err);
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Erro ao salvar o comentário: " + e.getMessage());
            return "redirect:/issues";
        }
    }
}