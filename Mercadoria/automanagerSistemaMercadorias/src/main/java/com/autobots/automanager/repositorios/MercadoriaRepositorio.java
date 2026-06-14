package com.autobots.automanager.repositorios;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.autobots.automanager.entidades.Mercadoria;

public interface MercadoriaRepositorio  extends JpaRepository<Mercadoria, Long> {
    List<Mercadoria> findByEmpresaId(Long empresaId);
    
}
