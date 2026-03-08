package com.example.demo.Repository;

import com.example.demo.Modelo.Fiabilidade;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional; // Importar

public interface FiabilidadeRepository extends JpaRepository<Fiabilidade, Long> {
    
    // Encontra um turno específico para um equipamento em uma data
    Optional<Fiabilidade> findByEquipamentoIdAndData(Long equipamentoId, LocalDate data);

    // Para o dashboard, pode ser útil buscar por um período
    List<Fiabilidade> findByDataBetween(LocalDate inicio, LocalDate fim);
    
    // Mantém o método antigo se o dashboard ainda o usar
    List<Fiabilidade> findByData(LocalDate data);

}
