package com.example.demo.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.Modelo.Mecanico;

public interface MecanicoRepository extends JpaRepository<Mecanico, Long> {

   Optional <Mecanico> findById(Long id);
    
}
