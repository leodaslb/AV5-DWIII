package com.autobots.automanager.repositorios;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.autobots.automanager.entidades.Veiculo;

public interface VeiculoRepositorio extends JpaRepository<Veiculo, Long> {
    
    
    List<Veiculo> findByEmpresaId(Long empresaId);
    
}