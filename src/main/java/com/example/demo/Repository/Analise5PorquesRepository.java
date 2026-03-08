package com.example.demo.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.Modelo.Analise5Porques;

public interface Analise5PorquesRepository extends JpaRepository<Analise5Porques, Long> {

    List<Analise5Porques> findByUsuarioAnaliseUsername(String username);

    List<Analise5Porques> findAllByOrderByDataInicioAvariaDesc();
    
}
