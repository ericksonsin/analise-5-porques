package com.example.demo.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.Modelo.User;
import com.example.demo.Repository.UserRepository;
import com.example.demo.Repository.Usuario;
import com.example.demo.Repository.UsuarioRepository;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    private final PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository usuarioRepository;

    public UsuarioController(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public String listar(Model model, Authentication authentication) {

        model.addAttribute("usuarios", usuarioRepository.findAll());
        model.addAttribute("usuarioLogado", authentication.getName());

        return "usuarios/usuarios";
    }

    @PostMapping("/salvar")
    public String salvar(@RequestParam(required = false) Long id,
            @RequestParam String username,
            @RequestParam(required = false) String password,
            @RequestParam String role,
            @RequestParam(defaultValue = "false") Boolean ativo,
            RedirectAttributes redirectAttributes) {

        try {

            // 🔎 validação de username duplicado
            if (id == null) {
                if (usuarioRepository.existsByUsername(username)) {
                    redirectAttributes.addFlashAttribute("mensagemErro",
                            "Já existe um usuário com esse username.");
                    return "redirect:/usuarios";
                }
            } else {
                if (usuarioRepository.existsByUsernameAndIdNot(username, id)) {
                    redirectAttributes.addFlashAttribute("mensagemErro",
                            "Já existe outro usuário com esse username.");
                    return "redirect:/usuarios";
                }
            }

            User usuario;

            if (id != null) {
                usuario = usuarioRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
            } else {
                usuario = new User();
            }

            usuario.setUsername(username);
            usuario.setRole(role);

            if (id == null) {
                // Se for cadastro novo, forçamos true (ou usamos o 'ativo' se preferir)
                usuario.setAtivo(true);
            } else {
                // Se for edição, usamos o valor que veio do checkbox (Boolean ativo)
                // Usamos defaultValue no @RequestParam ou tratamos o nulo aqui
                usuario.setAtivo(ativo != null && ativo);
            }

            // 🔐 Só altera senha se for preenchida
            if (password != null && !password.trim().isEmpty()) {
                usuario.setPassword(passwordEncoder.encode(password));
            }

            usuarioRepository.save(usuario);

            redirectAttributes.addFlashAttribute("mensagemSucesso",
                    id == null ? "Usuário criado com sucesso!" : "Usuário atualizado com sucesso!");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensagemErro",
                    "Erro ao salvar usuário.");
        }

        return "redirect:/usuarios";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id,
            Model model,
            Authentication authentication) {

        User usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        model.addAttribute("usuario", usuario);
        model.addAttribute("usuarios", usuarioRepository.findAll());
        model.addAttribute("usuarioLogado", authentication.getName());

        return "usuarios/usuarios";
    }

}
