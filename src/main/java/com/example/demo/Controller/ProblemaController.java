package com.example.demo.Controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.example.demo.Modelo.Analise5Porques;
import com.example.demo.Modelo.Subconjunto;
import com.example.demo.Repository.Analise5PorquesRepository;
import com.example.demo.Repository.EquipamentoRepository;
import com.example.demo.Repository.MecanicoRepository;
import com.example.demo.Repository.SubconjuntoRepository;
import com.example.demo.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;

@Controller
@RequestMapping("/problema")
public class ProblemaController {

    @Value("${app.upload.dir}")
    private String uploadDir;

    @Autowired
    private EquipamentoRepository equipamentoRepository;
    @Autowired
    private Analise5PorquesRepository analiseRepository;
    @Autowired
    private MecanicoRepository mecanicoRepository;
    @Autowired
    private SubconjuntoRepository subconjuntoRepository;
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/listar-problemas")
    public String listar(Model model, Authentication authentication) {

        boolean isAdmin = authentication.getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) {
            model.addAttribute("analises", analiseRepository.findAll());
        } else {
            model.addAttribute("analises",
                    analiseRepository.findByUsuarioAnaliseUsername(authentication.getName()));
        }

        return "problema/problema-lista";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("analise", new Analise5Porques());
        model.addAttribute("mecanicos", mecanicoRepository.findAll());
        model.addAttribute("equipamentos", equipamentoRepository.findAll());
        return "problema/problema-form";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        Analise5Porques analise = analiseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID inválido: " + id));

        model.addAttribute("analise", analise);
        model.addAttribute("equipamentos", equipamentoRepository.findAll());
        model.addAttribute("mecanicos", mecanicoRepository.findAll());

        // Importante: Carrega subconjuntos para o select não iniciar vazio na edição
        if (analise.getEquipamento() != null) {
            model.addAttribute("subconjuntos", analise.getEquipamento().getSubconjuntos());
        }
        return "problema/problema-form";
    }

    @PostMapping({ "/salvar", "/editar/{id}" })
    public String salvarOuAtualizar(
            @PathVariable(required = false) Long id,
            @ModelAttribute("analise") Analise5Porques analise,
            @RequestParam("foto") MultipartFile foto,
            @RequestParam("mecanicoId") Long mecanicoId,
            @RequestParam("equipamentoId") Long equipamentoId,
            @RequestParam("subconjuntoId") Long subconjuntoId,
            Authentication authentication,
            RedirectAttributes redirectAttributes) throws IOException {

        Analise5Porques analiseDB;
        if (id != null) {
            analiseDB = analiseRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("ID inválido: " + id));
            // Copia campos do form para o objeto do banco, exceto IDs e Imagem (se vazia)
            BeanUtils.copyProperties(analise, analiseDB, "id", "caminhoImagem", "usuarioAnalise");
        } else {
            analiseDB = analise;
            analiseDB.setUsuarioAnalise(userRepository.findByUsername(authentication.getName()));
        }

        analiseDB.setEquipamento(equipamentoRepository.findById(equipamentoId).get());
        analiseDB.setSubconjunto(subconjuntoRepository.findById(subconjuntoId).get());
        analiseDB.setMecanico(mecanicoRepository.findById(mecanicoId).get());

        if (!foto.isEmpty()) {
            String nomeArquivo = UUID.randomUUID() + "_" + foto.getOriginalFilename();
            Path uploadPath = Paths.get(uploadDir);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Files.copy(
                    foto.getInputStream(),
                    uploadPath.resolve(nomeArquivo),
                    StandardCopyOption.REPLACE_EXISTING);

            analiseDB.setCaminhoImagem(nomeArquivo);
        }

        analiseRepository.save(analiseDB);
        redirectAttributes.addFlashAttribute("mensagemSucesso", "Registro processado com sucesso!");
        return "redirect:/problema/listar-problemas";
    }

    @GetMapping("/subconjuntos/{equipamentoId}")
    @ResponseBody
    public List<Subconjunto> listarSubconjuntos(@PathVariable Long equipamentoId) {
        return equipamentoRepository.findById(equipamentoId)
                .map(e -> e.getSubconjuntos())
                .orElse(Collections.emptyList());
    }

    @PostMapping("/excluir/{id}")
    public String excluir(@PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        Analise5Porques analise = analiseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID inválido: " + id));

        if (analise.getCaminhoImagem() != null && !analise.getCaminhoImagem().isEmpty()) {
            try {
                Path caminhoImagem = Paths.get(uploadDir).resolve(analise.getCaminhoImagem());
                Files.deleteIfExists(caminhoImagem);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        analiseRepository.delete(analise);

        redirectAttributes.addFlashAttribute("mensagemSucesso", "Registro excluído com sucesso!");
        return "redirect:/problema/listar-problemas";
    }

}