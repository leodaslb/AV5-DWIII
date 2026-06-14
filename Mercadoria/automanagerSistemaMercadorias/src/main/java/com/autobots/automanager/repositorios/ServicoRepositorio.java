package com.autobots.automanager.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import com.autobots.automanager.entidades.Servico;
import com.autobots.automanager.entidades.Usuario;

public interface ServicoRepositorio  extends JpaRepository<Servico, Long> {
    List<Servico> findByEmpresaId(Long empresaId);
}
