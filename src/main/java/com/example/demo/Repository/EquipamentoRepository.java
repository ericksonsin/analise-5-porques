package com.example.demo.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.Modelo.Equipamento;

public interface EquipamentoRepository extends JpaRepository<Equipamento, Long> {
    
}
