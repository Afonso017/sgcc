package com.desafiodunnas.sgcc.controller;

import com.desafiodunnas.sgcc.domain.Block;
import com.desafiodunnas.sgcc.service.BlockService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller responsável pelo CRUD e gerenciamento da infraestrutura física principal (blocos).
 */
@Controller
@RequestMapping("/admin/blocos")
@RequiredArgsConstructor
public class BlockController {

    private final BlockService blockService;

    @GetMapping
    public String listBlocks(@RequestParam(value = "q", required = false) String keyword, Model model) {
        try {
            model.addAttribute("blocks", blockService.searchBlocks(keyword));
            model.addAttribute("keyword", keyword);
            return "admin/blocks/list";
        } catch (Exception e) {
            System.err.println("Erro ao carregar a listagem de blocos");
            e.printStackTrace(System.err);
            model.addAttribute("errorMessage", "Ocorreu um erro ao carregar a lista de blocos.");
            return "error";
        }
    }

    @GetMapping("/novo")
    public String newBlockForm(Model model) {
        try {
            model.addAttribute("block", new Block());
            return "admin/blocks/form";
        } catch (Exception e) {
            System.err.println("Erro ao renderizar o formulário de blocos");
            e.printStackTrace(System.err);
            model.addAttribute("errorMessage", "Ocorreu um erro ao abrir o formulário.");
            return "error";
        }
    }

    /**
     * Cria um novo bloco e dispara a geração em massa (batch insert)
     * de todas as unidades contidas nele via lógica de serviço assíncrona.
     */
    @PostMapping
    public String createBlock(@ModelAttribute Block block, Model model, RedirectAttributes redirectAttributes) {
        try {
            blockService.createBlock(block);

            redirectAttributes.addFlashAttribute("successMessage",
                    "A criação do bloco e das unidades foi iniciada em segundo plano. " +
                            "Dependendo do tamanho, as unidades estarão disponíveis no sistema em alguns instantes.");

            return "redirect:/admin/blocos";
        } catch (Exception e) {
            System.err.println("Erro ao salvar um novo bloco no banco de dados");
            e.printStackTrace(System.err);
            model.addAttribute("errorMessage",
                    "Ocorreu um erro ao salvar o bloco: " + e.getMessage());
            return "admin/blocks/form";
        }
    }

    @GetMapping("/{id}/editar")
    public String editBlockForm(@PathVariable Long id, Model model) {
        try {
            model.addAttribute("block", blockService.findBlockById(id));
            return "admin/blocks/form";
        } catch (Exception e) {
            System.err.println("Erro ao carregar o bloco para edição");
            e.printStackTrace(System.err);
            model.addAttribute("errorMessage", "Ocorreu um erro ao abrir o formulário de edição.");
            return "error";
        }
    }

    @PostMapping("/{id}/editar")
    public String updateBlock(@PathVariable Long id, @ModelAttribute Block block, RedirectAttributes redirectAttributes) {
        try {
            blockService.updateBlock(id, block);
            return "redirect:/admin/blocos";
        } catch (Exception e) {
            System.err.println("Erro ao atualizar o bloco");
            e.printStackTrace(System.err);
            redirectAttributes.addFlashAttribute("errorMessage",
                    e.getCause() != null ? e.getCause().getMessage() : "Erro ao atualizar o bloco.");
            return "redirect:/admin/blocos/" + id + "/editar";
        }
    }

    /**
     * Exclui um bloco e propaga a remoção em cascata para todas
     * as unidades habitacionais vinculadas a ele.
     */
    @PostMapping("/{id}/excluir")
    public String deleteBlock(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            blockService.deleteBlock(id);
        } catch (DataIntegrityViolationException e) {
            String errorMessage = "Ação negada: Este bloco possui vínculo com chamados ou moradores registrados " +
                    "no sistema e não pode ser excluído.";
            System.err.println(errorMessage);
            e.printStackTrace(System.err);
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
        } catch (Exception e) {
            System.err.println("Erro ao processar exclusão do bloco no controller");
            e.printStackTrace(System.err);
            redirectAttributes.addFlashAttribute("errorMessage",
                    e.getCause() != null ? e.getCause().getMessage() : "Não foi possível excluir o bloco.");
        }
        return "redirect:/admin/blocos";
    }
}