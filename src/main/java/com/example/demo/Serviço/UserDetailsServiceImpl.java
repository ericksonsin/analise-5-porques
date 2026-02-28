package com.example.demo.Serviço;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.example.demo.Modelo.User;
import com.example.demo.Repository.UserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    private final Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // logger.info("Tentativa de login do usuário: {}", username);
        User user = userRepository.findByUsername(username);
        if (user == null) {
            // logger.warn("Usuário não encontrado: {}", username);
            throw new UsernameNotFoundException("Usuário não encontrado");
        }

        // Verifique se o papel (role) do usuário não está vazio
        if (user.getRole() == null || user.getRole().isEmpty()) {
            throw new IllegalArgumentException("O usuário não possui um papel válido.");
        }

        // Retorna as autoridades do usuário, garantindo que o formato esteja correto
        // logger.info("Usuário encontrado: {}, senha hash: {}", user.getUsername(),
        // user.getPassword());
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                AuthorityUtils.createAuthorityList(user.getRole())
        // Garante que o papel tenha o prefixo 'ROLE_'
        );
    }
}
