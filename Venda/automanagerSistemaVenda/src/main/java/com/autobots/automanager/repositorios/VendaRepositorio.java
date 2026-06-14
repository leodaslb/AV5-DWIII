package com.autobots.automanager.repositorios;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.autobots.automanager.entidades.Venda;

public interface VendaRepositorio  extends JpaRepository<Venda, Long> {
    List<Venda> findByEmpresaIdAndCadastroBetween(Long empresaId, Date dataInicial, Date dataFinal);
    
}
