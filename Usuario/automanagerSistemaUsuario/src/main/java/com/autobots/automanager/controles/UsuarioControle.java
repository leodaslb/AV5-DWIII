package com.autobots.automanager.controles;

import java.security.Principal;
import java.util.List;

import javax.xml.bind.PrintConversionEvent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.autobots.automanager.dto.LoginReqDto;
import com.autobots.automanager.dto.UsuarioReqDto;
import com.autobots.automanager.dto.UsuarioResDto;
import com.autobots.automanager.entidades.Usuario;
import com.autobots.automanager.modelos.AdicionadorLinkUsuario;
import com.autobots.automanager.modelos.Perfil;
import com.autobots.automanager.repositorios.UsuarioRepositorio;
import com.autobots.automanager.services.UsuarioServico;

@RestController
@RequestMapping("/usuario")
public class UsuarioControle {

    @Autowired
    private UsuarioServico usuarioServico;
    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Autowired
    private AdicionadorLinkUsuario adicionadorLink;


    @GetMapping("/empresa/{empresaId}/clientes")
public ResponseEntity<List<UsuarioResDto>> obterClientesPorEmpresa(@PathVariable Long empresaId) {
    List<UsuarioResDto> clientes = usuarioServico.listarClientesPorEmpresa(empresaId);
    if (clientes.isEmpty()) {
        return ResponseEntity.noContent().build();
    }
    adicionadorLink.adicionarLink(clientes);
    return ResponseEntity.ok(clientes);
}

    @GetMapping("/empresa/{empresaId}/funcionarios")
    public ResponseEntity<List<UsuarioResDto>> obterFuncionariosPorEmpresa(@PathVariable Long empresaId) {
    List<UsuarioResDto> funcionarios = usuarioServico.listarFuncionariosPorEmpresa(empresaId);
    if (funcionarios.isEmpty()) {
        return ResponseEntity.noContent().build();
    }
    adicionadorLink.adicionarLink(funcionarios);
    return ResponseEntity.ok(funcionarios);
    }
    

    @GetMapping("/usuarios")
    
    public ResponseEntity<List<UsuarioResDto>> obterUsuarios() {
        List<Usuario> usuarios = usuarioServico.selecionarTodos();
        if (usuarios.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        List<UsuarioResDto> dto = usuarioServico.toResDtoList(usuarios);
        adicionadorLink.adicionarLink(dto);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/porNome/{nomeUsuario}")
    public ResponseEntity<UsuarioResDto> obterPorNome(@PathVariable String nomeUsuario) {
    Usuario usuario = usuarioServico.selecionarPorNome(nomeUsuario);
    if (usuario == null) return ResponseEntity.notFound().build();
    UsuarioResDto dto = usuarioServico.toResDto(usuario);
    return ResponseEntity.ok(dto);
}
    
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResDto> obterUsuario(@PathVariable Long id, Principal principal) {
    Usuario usuario = usuarioServico.selecionarPorId(id);
    if (usuario == null) {
        return ResponseEntity.notFound().build();
    }

        
    UsuarioResDto dto = usuarioServico.toResDto(usuario);
    adicionadorLink.adicionarLink(dto);
    return ResponseEntity.ok(dto);
}

    @PostMapping("/cadastrar")
    public ResponseEntity<UsuarioResDto> criarUsuario(@RequestBody UsuarioReqDto dtoReq, Principal principal) {
        Usuario usuario = usuarioServico.criarUsuario(dtoReq);

        Usuario usuarioLogado = usuarioRepositorio
        .findByCredencialNomeUsuario(principal.getName());
        boolean isVendedor = usuarioLogado.getPerfis().contains(Perfil.ROLE_VENDEDOR);

        if (isVendedor && !usuario.getPerfis().contains(Perfil.ROLE_CLIENTE)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
        boolean isGerente = usuarioLogado.getPerfis().contains(Perfil.ROLE_GERENTE);
        if (isGerente && usuario.getPerfis().contains(Perfil.ROLE_ADMIN)) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }


        UsuarioResDto dtoRes = usuarioServico.toResDto(usuario);
        adicionadorLink.adicionarLink(dtoRes);
        return ResponseEntity.status(HttpStatus.CREATED).body(dtoRes);
    }

    @PutMapping("/atualizar/{id}")
    public ResponseEntity<UsuarioResDto> atualizarUsuario(@PathVariable Long id, @RequestBody UsuarioReqDto dtoReq, Principal principal) {
        Usuario usuario = usuarioServico.atualizar(id, dtoReq);
        if (usuario == null) {
            return ResponseEntity.notFound().build();
        }
        Usuario usuarioLogado = usuarioRepositorio
        .findByCredencialNomeUsuario(principal.getName());
        boolean isVendedor = usuarioLogado.getPerfis().contains(Perfil.ROLE_VENDEDOR);
        boolean isGerente = usuarioLogado.getPerfis().contains(Perfil.ROLE_GERENTE);
        if (isGerente && usuario.getPerfis().contains(Perfil.ROLE_ADMIN)) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

        if (isVendedor && !usuario.getPerfis().contains(Perfil.ROLE_CLIENTE)) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
        UsuarioResDto dtoRes = usuarioServico.toResDto(usuario);
        adicionadorLink.adicionarLink(dtoRes);
        return ResponseEntity.ok(dtoRes);
    }

    @DeleteMapping("/deletar/{id}")
    public ResponseEntity<Void> excluirUsuario(@PathVariable Long id, Principal principal) {
        Usuario usuario = usuarioServico.selecionarPorId(id);
        Usuario usuarioLogado = usuarioRepositorio
        .findByCredencialNomeUsuario(principal.getName());
        boolean isVendedor = usuarioLogado.getPerfis().contains(Perfil.ROLE_VENDEDOR);
        boolean isGerente = usuarioLogado.getPerfis().contains(Perfil.ROLE_GERENTE);
        if (isGerente && usuario.getPerfis().contains(Perfil.ROLE_ADMIN)) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

        if (isVendedor && !usuario.getPerfis().contains(Perfil.ROLE_CLIENTE)) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
        
        usuarioServico.excluir(id);
        return ResponseEntity.ok().build();
    }
}