package com.example.demo.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.example.demo.Modelo.Subconjunto;
import com.example.demo.Repository.SubconjuntoRepository;

@Controller
@RequestMapping("/subconjuntos")
public class SubconjuntoController {

    @Autowired
    private SubconjuntoRepository subconjuntoRepository;

    @GetMapping
    public String listar(Model model,
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page) {

        List<Subconjunto> subconjuntos = subconjuntoRepository.findAll();

        model.addAttribute("subconjuntos", subconjuntos);
        model.addAttribute("usuarioLogado", authentication.getName());

        return "subconjuntos/subconjuntos";
    }

    @PostMapping("/salvar")
    public String salvar(@RequestParam(required = false) Long id,
            @RequestParam String nome,
            @RequestParam(required = false) String descricao,
            RedirectAttributes redirectAttributes) {

        try {

            // 🔎 VALIDAÇÃO CORRETA PARA EDIÇÃO
            if (id == null) {
                // cadastro novo
                if (subconjuntoRepository.existsByNomeIgnoreCase(nome)) {

                    redirectAttributes.addFlashAttribute(
                            "mensagemErro",
                            "Já existe um subconjunto com esse nome!");

                    return "redirect:/subconjuntos";
                }
            } else {
                // edição
                if (subconjuntoRepository
                        .existsByNomeIgnoreCaseAndIdNot(nome, id)) {

                    redirectAttributes.addFlashAttribute(
                            "mensagemErro",
                            "Já existe outro subconjunto com esse nome!");

                    return "redirect:/subconjuntos";
                }
            }

            Subconjunto subconjunto;

            if (id != null) {
                subconjunto = subconjuntoRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Subconjunto não encontrado"));
            } else {
                subconjunto = new Subconjunto();
            }

            subconjunto.setNome(nome);
            subconjunto.setDescricao(descricao);

            subconjuntoRepository.save(subconjunto);

            redirectAttributes.addFlashAttribute(
                    "mensagemSucesso",
                    id == null ? "Subconjunto cadastrado!" : "Subconjunto atualizado!");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                    "mensagemErro",
                    "Erro ao salvar subconjunto.");
        }

        return "redirect:/subconjuntos";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id,
            Model model,
            Authentication authentication) {

        Subconjunto subconjunto = subconjuntoRepository
                .findById(id)
                .orElseThrow(() -> new RuntimeException("Subconjunto não encontrado"));

        model.addAttribute("subconjunto", subconjunto);
        model.addAttribute("subconjuntos", subconjuntoRepository.findAll());
        model.addAttribute("usuarioLogado", authentication.getName());

        return "subconjuntos/subconjuntos";
    }

}