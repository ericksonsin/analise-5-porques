package com.example.demo.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.Modelo.User;

public interface UsuarioRepository extends JpaRepository<User, Long> {

    
    
}
