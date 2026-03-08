package com.example.demo.Controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.Modelo.Equipamento;
import com.example.demo.Modelo.Subconjunto;
import com.example.demo.Repository.EquipamentoRepository;
import com.example.demo.Repository.SubconjuntoRepository;

@Controller
@RequestMapping("/equipamentos")
public class EquipamentoController {

    @Autowired
    private SubconjuntoRepository subconjuntoRepository;

    @Autowired
    private EquipamentoRepository equipamentoRepository;

    @GetMapping
    public String listar(Model model, Authentication authentication) {

        List<Subconjunto> subconjuntos = subconjuntoRepository.findAll();
        model.addAttribute("subconjuntos", subconjuntos);
        model.addAttribute("equipamentos", equipamentoRepository.findAll());
        model.addAttribute("usuarioLogado", authentication.getName());

        return "equipamentos/equipamentos";
    }

    @PostMapping("/salvar")
    public String salvar(@RequestParam(required = false) Long id, Integer producaoPorHora,
            @RequestParam String nome,
            @RequestParam(required = false) List<Long> subconjuntosIds,
            RedirectAttributes redirectAttributes) {

        try {

            Equipamento equipamento;

            if (id != null) {
                equipamento = equipamentoRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Equipamento não encontrado"));
            } else {
                equipamento = new Equipamento();
            }

            equipamento.setNome(nome);
            equipamento.setProducaoPorHora (producaoPorHora);

            if (subconjuntosIds != null) {
                List<Subconjunto> subconjuntos = subconjuntoRepository.findAllById(subconjuntosIds);
                equipamento.setSubconjuntos(subconjuntos);
            } else {
                equipamento.setSubconjuntos(new ArrayList<>());
            }

            equipamentoRepository.save(equipamento);

            redirectAttributes.addFlashAttribute(
                    "mensagemSucesso",
                    id == null ? "Equipamento cadastrado!" : "Equipamento atualizado!");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                    "mensagemErro",
                    "Erro ao salvar equipamento.");
        }

        return "redirect:/equipamentos";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id,
            Model model,
            Authentication authentication) {

        Equipamento equipamento = equipamentoRepository
                .findById(id)
                .orElseThrow(() -> new RuntimeException("Equipamento não encontrado"));

        model.addAttribute("equipamento", equipamento);
        model.addAttribute("subconjuntos", subconjuntoRepository.findAll());
        model.addAttribute("equipamentos", equipamentoRepository.findAll());
        model.addAttribute("usuarioLogado", authentication.getName());

        return "equipamentos/equipamentos";
    }

}
