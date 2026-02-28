package com.example.demo.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.Modelo.Mecanico;
import com.example.demo.Repository.MecanicoRepository;

@Controller
@RequestMapping("/mecanicos")
public class MecanicoController {

    @Autowired
    private MecanicoRepository mecanicoRepository;

    @GetMapping("/novo")
    public String listar(Model model) {
        model.addAttribute("mecanico", new Mecanico()); // Objeto vazio para o form de cadastro
        model.addAttribute("mecanicos", mecanicoRepository.findAll());
        return "mecanicos/mecanicos";
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Mecanico mecanico, RedirectAttributes ra) {
        mecanicoRepository.save(mecanico);
        ra.addFlashAttribute("mensagemSucesso", "Mecânico salvo com sucesso!");
        return "redirect:/mecanicos/novo";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        Mecanico mecanico = mecanicoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mecânico não encontrado"));
        
        model.addAttribute("mecanico", mecanico);
        model.addAttribute("mecanicos", mecanicoRepository.findAll());
        return "mecanicos/mecanicos";
    }
}
