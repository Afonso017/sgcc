package com.desafiodunnas.sgcc.controller;

import com.desafiodunnas.sgcc.domain.IssueStatus;
import com.desafiodunnas.sgcc.repository.IssueStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Controller administrativo para a criação e manutenção do fluxo de estados dos chamados.
 */
@Controller
@RequestMapping("/admin/status-chamados")
@RequiredArgsConstructor
public class IssueStatusController {

    private final IssueStatusRepository issueStatusRepository;

    @GetMapping
    public String list(Model model) {
        try {
            model.addAttribute("statuses", issueStatusRepository.findAll());
            return "admin/issues/statuses";
        } catch (Exception e) {
            System.err.println("Erro ao listar status de chamados");
            e.printStackTrace(System.err);
            return "error";
        }
    }

    @PostMapping
    public String save(@ModelAttribute IssueStatus status,
                       @RequestParam(required = false) String configType,
                       RedirectAttributes redirectAttributes
    ) {
        try {
            if (status.getId() == null && issueStatusRepository.existsByNameIgnoreCase(status.getName())) {
                redirectAttributes.addFlashAttribute("errorMessage",
                        "Já existe um status cadastrado com este nome!");
                return "redirect:/admin/status-chamados";
            }

            boolean isStart = "START".equals(configType);
            boolean isEnd = "END".equals(configType);

            status.setIsDefault(isStart);
            status.setIsFinal(isEnd);

            // Ao marcar este como padrão, retira o padrão dos outros.
            if (isStart) {
                List<IssueStatus> all = issueStatusRepository.findAll();
                all.forEach(s -> s.setIsDefault(false));
                issueStatusRepository.saveAll(all);
            }

            issueStatusRepository.save(status);
            return "redirect:/admin/status-chamados";
        } catch (Exception e) {
            System.err.println("Erro ao salvar status de chamado");
            e.printStackTrace(System.err);
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao salvar o status.");
            return "redirect:/admin/status-chamados";
        }
    }

    @PostMapping("/{id}/excluir")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            issueStatusRepository.deleteById(id);
            return "redirect:/admin/status-chamados";
        } catch (Exception e) {
            System.err.println("Erro ao excluir status de chamado");
            e.printStackTrace(System.err);
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Não é possível excluir um status que já está em uso por chamados.");
            return "redirect:/admin/status-chamados";
        }
    }
}