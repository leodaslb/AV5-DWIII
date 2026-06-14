package com.autobots.automanager.repositorios;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.autobots.automanager.entidades.Usuario;

public interface UsuarioRepositorio  extends JpaRepository<Usuario, Long> {
    Usuario findByCredencialNomeUsuario(String nomeUsuario);
    List<Usuario> findByEmpresaId(Long empresaId);
    
}
