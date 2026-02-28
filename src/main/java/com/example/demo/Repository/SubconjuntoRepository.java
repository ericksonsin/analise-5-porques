package com.example.demo.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.Modelo.Subconjunto;

public interface SubconjuntoRepository  extends JpaRepository<Subconjunto, Long> {



    boolean existsByNomeIgnoreCaseAndIdNot(String nome, Long id);

    boolean existsByNomeIgnoreCase(String nome);

    
}
