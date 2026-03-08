package com.example.demo.Repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.Modelo.FiabilidadeRegistro;


public interface FiabilidadeRegistroRepository extends JpaRepository<FiabilidadeRegistro, Long> {

    List<FiabilidadeRegistro> findByFiabilidadeId(Long fiabilidadeId);

}

