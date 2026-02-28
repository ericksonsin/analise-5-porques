package com.example.demo.Modelo;

import java.time.LocalDateTime;

import javax.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;
    private String role; 
    private Boolean ativo = true;
    private LocalDateTime dataCriacao = LocalDateTime.now();
}
