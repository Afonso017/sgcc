package com.desafiodunnas.sgcc.controller;

import com.desafiodunnas.sgcc.domain.User;
import com.desafiodunnas.sgcc.domain.UserRole;
import com.desafiodunnas.sgcc.service.BlockService;
import com.desafiodunnas.sgcc.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller responsável pelo gerenciamento de usuários do sistema (CRUD)
 * e pela gestão dos vínculos entre moradores e unidades.
 */
@Controller
@RequestMapping("/admin/usuarios")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final BlockService blockService;

    /**
     * Lista todos os usuários, aplicando o filtro de pesquisa se fornecido.
     * Oculta o próprio usuário logado da listagem para evitar excluir a si próprio.
     */
    @GetMapping
    public String listUsers(@RequestParam(value = "q", required = false) String keyword,
                            @ModelAttribute("currentUser") User currentUser,
                            Model model
    ) {
        try {
            List<User> users = userService.searchUsers(keyword).stream()
                    .filter(user -> !user.getId().equals(currentUser.getId()))
                    .collect(Collectors.toList());

            model.addAttribute("users", users);
            model.addAttribute("blocks", blockService.findAllBlocks());
            model.addAttribute("keyword", keyword);
            return "admin/users/list";
        } catch (Exception e) {
            System.err.println("Erro ao carregar a listagem de usuários");
            e.printStackTrace(System.err);
            model.addAttribute("errorMessage",
                    "Ocorreu um erro ao carregar a lista de usuários.");
            return "error";
        }
    }

    @GetMapping("/novo")
    public String newUserForm(Model model) {
        try {
            model.addAttribute("user", new User());
            model.addAttribute("roles", UserRole.values());
            return "admin/users/form";
        } catch (Exception e) {
            System.err.println("Erro ao renderizar o formulário de usuário");
            e.printStackTrace(System.err);
            model.addAttribute("errorMessage", "Ocorreu um erro ao abrir o formulário.");
            return "error";
        }
    }

    @PostMapping
    public String createUser(@ModelAttribute User user, Model model) {
        try {
            userService.createUser(user);
            return "redirect:/admin/usuarios";
        } catch (Exception e) {
            System.err.println("Erro ao salvar um novo usuário");
            e.printStackTrace(System.err);
            model.addAttribute("errorMessage",
                    "Ocorreu um erro ao salvar o usuário: " + e.getMessage());
            model.addAttribute("roles", UserRole.values());
            return "admin/users/form";
        }
    }

    @GetMapping("/{id}/editar")
    public String editUserForm(@PathVariable Long id, Model model) {
        try {
            model.addAttribute("user", userService.findUserById(id));
            model.addAttribute("roles", UserRole.values());
            return "admin/users/form";
        } catch (Exception e) {
            System.err.println("Erro ao carregar o usuário para edição");
            e.printStackTrace(System.err);
            model.addAttribute("errorMessage",
                    "Ocorreu um erro ao abrir o formulário de edição.");
            return "error";
        }
    }

    @PostMapping("/{id}/editar")
    public String updateUser(@PathVariable Long id,
                             @ModelAttribute User user,
                             RedirectAttributes redirectAttributes
    ) {
        try {
            userService.updateUser(id, user);
            return "redirect:/admin/usuarios";
        } catch (Exception e) {
            System.err.println("Erro ao atualizar o usuário");
            e.printStackTrace(System.err);
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Erro ao atualizar o usuário.");
            return "redirect:/admin/usuarios/" + id + "/editar";
        }
    }

    @PostMapping("/{id}/excluir")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.deleteUser(id);
            return "redirect:/admin/usuarios";
        } catch (Exception e) {
            System.err.println("Erro ao processar exclusão de usuário");
            e.printStackTrace(System.err);
            redirectAttributes.addFlashAttribute("errorMessage",
                    e.getCause() != null ? e.getCause().getMessage() : "Não foi possível excluir o usuário.");
            return "redirect:/admin/usuarios";
        }
    }

    /**
     * Vincula uma unidade habitacional existente a um usuário (morador).
     */
    @PostMapping("/{userId}/vincular-unidade")
    public String linkUnit(@PathVariable Long userId,
                           @RequestParam Long unitId,
                           RedirectAttributes redirectAttributes
    ) {
        if (unitId == null) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Atenção: Você precisa selecionar uma unidade antes de clicar em vincular.");
            return "redirect:/admin/usuarios";
        }

        try {
            userService.linkUserToUnit(userId, unitId);
            return "redirect:/admin/usuarios";
        } catch (Exception e) {
            System.err.println("Erro ao vincular a unidade ao usuário");
            e.printStackTrace(System.err);
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Ocorreu um erro ao vincular a unidade.");
            return "redirect:/admin/usuarios";
        }
    }

    /**
     * Desvincula uma unidade habitacional de um usuário.
     */
    @PostMapping("/{userId}/desvincular-unidade")
    public String unlinkUnit(@PathVariable Long userId,
                             @RequestParam Long unitId,
                             RedirectAttributes redirectAttributes
    ) {
        try {
            userService.unlinkUserFromUnit(userId, unitId);
            return "redirect:/admin/usuarios";
        } catch (Exception e) {
            System.err.println("Erro ao desvincular a unidade do usuário");
            e.printStackTrace(System.err);
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Ocorreu um erro ao remover a unidade.");
            return "redirect:/admin/usuarios";
        }
    }
}