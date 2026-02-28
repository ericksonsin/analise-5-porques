package com.example.demo.Controller;

import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.ui.Model;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String login(HttpServletRequest request, Model model) {
       request.getSession(true);
        return "login";
    }

    @GetMapping("/")
    public String redirectToLogin() {
        return "redirect:/login";
    }

    // Método POST para processar o login e validar o reCAPTCHA
    // @PostMapping("/process-login")
    // public String loginPost(@RequestParam("username") String username,
    // @RequestParam("password") String password,
    // @RequestParam("g-recaptcha-response") String recaptchaResponse,
    // HttpServletRequest request, Model model) {

    // // Verifica o reCAPTCHA
    // if (!verifyRecaptcha(recaptchaResponse)) {
    // model.addAttribute("error", "Verificação do reCAPTCHA falhou!");
    // return "login"; // Redireciona de volta para a tela de login
    // }

    // // Carregar o usuário através do UserDetailsService
    // try {
    // UserDetails userDetails = userDetailsService.loadUserByUsername(username);

    // // Autenticar o usuário com as credenciais (usuário + senha)
    // UsernamePasswordAuthenticationToken authenticationToken = new
    // UsernamePasswordAuthenticationToken(
    // username, password, userDetails.getAuthorities());

    // // Autenticação no Spring Security
    // Authentication authentication =
    // authenticationManager.authenticate(authenticationToken);

    // // Se a autenticação for bem-sucedida, define o contexto de segurança
    // SecurityContextHolder.getContext().setAuthentication(authentication);

    // // Redirecionar para a página inicial após o login bem-sucedido
    // return "redirect:/home";

    // } catch (Exception e) {
    // model.addAttribute("error", "Usuário ou senha inválidos!");
    // return "login"; // Redireciona de volta à página de login com erro
    // }
    // }

    // private boolean verifyRecaptcha(String recaptchaResponse) {
    // String url = "https://www.google.com/recaptcha/api/siteverify";
    // String params = "secret=" + recaptchaSecret + "&response=" +
    // recaptchaResponse;

    // RestTemplate restTemplate = new RestTemplate();
    // ResponseEntity<String> response = restTemplate.postForEntity(url, params,
    // String.class);

    // // Verifica a resposta do Google
    // return response.getBody().contains("\"success\": true");
    // }
}
