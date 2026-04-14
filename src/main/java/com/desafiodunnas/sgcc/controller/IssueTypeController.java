package com.desafiodunnas.sgcc.controller;

import com.desafiodunnas.sgcc.domain.IssueType;
import com.desafiodunnas.sgcc.repository.IssueTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller administrativo para o gerenciamento de categorias de chamados e SLAs.
 */
@Controller
@RequestMapping("/admin/tipos-chamados")
@RequiredArgsConstructor
public class IssueTypeController {

    private final IssueTypeRepository issueTypeRepository;

    @GetMapping
    public String list(Model model) {
        try {
            model.addAttribute("types", issueTypeRepository.findAll());
            return "admin/issues/types";
        } catch (Exception e) {
            System.err.println("Erro ao listar tipos de chamados");
            e.printStackTrace(System.err);
            return "error";
        }
    }

    @PostMapping
    public String save(@ModelAttribute IssueType issueType, RedirectAttributes redirectAttributes) {
        try {
            if (issueType.getId() == null && issueTypeRepository.existsByTitleIgnoreCase(issueType.getTitle())) {
                redirectAttributes.addFlashAttribute("errorMessage",
                        "Já existe um tipo de chamado cadastrado com este título!");
                return "redirect:/admin/status-chamados";
            }

            if (issueType.getSlaHours() == null || issueType.getSlaHours() <= 0) {
                redirectAttributes.addFlashAttribute("errorMessage",
                        "O tempo de SLA deve ser de pelo menos 1 hora.");
                return "redirect:/admin/tipos-chamados";
            }

            issueTypeRepository.save(issueType);
            return "redirect:/admin/tipos-chamados";
        } catch (Exception e) {
            System.err.println("Erro ao salvar tipo de chamado");
            e.printStackTrace(System.err);
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao salvar o tipo.");
            return "redirect:/admin/tipos-chamados";
        }
    }

    @PostMapping("/{id}/excluir")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            issueTypeRepository.deleteById(id);
            return "redirect:/admin/tipos-chamados";
        } catch (Exception e) {
            System.err.println("Erro ao excluir tipo de chamado");
            e.printStackTrace(System.err);
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Não é possível excluir um tipo que já possui chamados vinculados.");
            return "redirect:/admin/tipos-chamados";
        }
    }
}