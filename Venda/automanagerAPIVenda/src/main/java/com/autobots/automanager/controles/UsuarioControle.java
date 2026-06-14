package com.autobots.automanager.controles;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import com.autobots.automanager.dto.UsuarioReqDto;
import com.autobots.automanager.dto.UsuarioResDto;
import com.autobots.automanager.modelos.Perfil;

import java.security.Principal;
import java.util.List;
@RestController
@RequestMapping("/usuario")
public class UsuarioControle {

    @Autowired
    private RestTemplate restTemplate;

    private static final String SISTEMA_URL = "http://localhost:8081/usuario";

    // busca o usuário logado pelo nome (helper privado)
    private UsuarioResDto getUsuarioLogado(Principal principal) {
        return restTemplate.getForObject(
            SISTEMA_URL + "/porNome/" + principal.getName(),
            UsuarioResDto.class
        );
    }

    @GetMapping("/usuarios")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<List<UsuarioResDto>> obterUsuarios() {
        ResponseEntity<List<UsuarioResDto>> response = restTemplate.exchange(
            SISTEMA_URL + "/usuarios",
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<UsuarioResDto>>() {}
        );
        return response;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResDto> obterUsuario(@PathVariable Long id, Principal principal) {
        UsuarioResDto usuarioLogado = getUsuarioLogado(principal);
        UsuarioResDto usuario = restTemplate.getForObject(SISTEMA_URL + "/" + id, UsuarioResDto.class);

        if (usuario == null) return ResponseEntity.notFound().build();

        boolean isCliente  = usuarioLogado.getPerfis().contains(Perfil.ROLE_CLIENTE);
        boolean isVendedor = usuarioLogado.getPerfis().contains(Perfil.ROLE_VENDEDOR);
        boolean isGerente  = usuarioLogado.getPerfis().contains(Perfil.ROLE_GERENTE);

        if (isCliente && !usuario.getId().equals(usuarioLogado.getId()))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        if (isGerente && usuario.getPerfis().contains(Perfil.ROLE_ADMIN))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        if (isVendedor && !usuario.getPerfis().contains(Perfil.ROLE_CLIENTE))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        return ResponseEntity.ok(usuario);
    }

    @PostMapping("/cadastrar")
    public ResponseEntity<UsuarioResDto> criarUsuario(@RequestBody UsuarioReqDto dtoReq, Principal principal) {
        UsuarioResDto usuarioLogado = getUsuarioLogado(principal);

        boolean isVendedor = usuarioLogado.getPerfis().contains(Perfil.ROLE_VENDEDOR);
        boolean isGerente  = usuarioLogado.getPerfis().contains(Perfil.ROLE_GERENTE);

        if (isVendedor && !dtoReq.getPerfis().contains(Perfil.ROLE_CLIENTE))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        if (isGerente && dtoReq.getPerfis().contains(Perfil.ROLE_ADMIN))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        ResponseEntity<UsuarioResDto> response = restTemplate.postForEntity(
            SISTEMA_URL + "/cadastrar",
            dtoReq,
            UsuarioResDto.class
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response.getBody());
    }

    @PutMapping("/atualizar/{id}")
    public ResponseEntity<UsuarioResDto> atualizarUsuario(@PathVariable Long id, @RequestBody UsuarioReqDto dtoReq, Principal principal) {
        UsuarioResDto usuarioLogado = getUsuarioLogado(principal);
        UsuarioResDto usuario = restTemplate.getForObject(SISTEMA_URL + "/" + id, UsuarioResDto.class);

        if (usuario == null) return ResponseEntity.notFound().build();

        boolean isVendedor = usuarioLogado.getPerfis().contains(Perfil.ROLE_VENDEDOR);
        boolean isGerente  = usuarioLogado.getPerfis().contains(Perfil.ROLE_GERENTE);

        if (isGerente && usuario.getPerfis().contains(Perfil.ROLE_ADMIN))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        if (isVendedor && !usuario.getPerfis().contains(Perfil.ROLE_CLIENTE))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        restTemplate.put(SISTEMA_URL + "/atualizar/" + id, dtoReq);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/deletar/{id}")
    public ResponseEntity<Void> excluirUsuario(@PathVariable Long id, Principal principal) {
        UsuarioResDto usuarioLogado = getUsuarioLogado(principal);
        UsuarioResDto usuario = restTemplate.getForObject(SISTEMA_URL + "/" + id, UsuarioResDto.class);

        if (usuario == null) return ResponseEntity.notFound().build();

        boolean isVendedor = usuarioLogado.getPerfis().contains(Perfil.ROLE_VENDEDOR);
        boolean isGerente  = usuarioLogado.getPerfis().contains(Perfil.ROLE_GERENTE);

        if (isGerente && usuario.getPerfis().contains(Perfil.ROLE_ADMIN))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        if (isVendedor && !usuario.getPerfis().contains(Perfil.ROLE_CLIENTE))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        restTemplate.delete(SISTEMA_URL + "/deletar/" + id);
        return ResponseEntity.ok().build();
    }
}