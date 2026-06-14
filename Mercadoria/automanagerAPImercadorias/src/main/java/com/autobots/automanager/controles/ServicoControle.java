package com.autobots.automanager.controles;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import com.autobots.automanager.dto.ServicoReqDto;
import com.autobots.automanager.dto.ServicoResDto;

@RestController
@RequestMapping("/servico")
public class ServicoControle {

    @Autowired
    private RestTemplate restTemplate;

    private static final String SISTEMA_SERVICOS_URL = "http://localhost:8083/servico";
    private static final String SISTEMA_USUARIOS_URL = "http://localhost:8081/usuario";

    private <T> HttpEntity<T> montarRequisicaoComHeaders(T body, Authentication authentication) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        if (authentication != null) {
            String nomeUsuario = authentication.getName();
            
            String perfis = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.joining(","));
            
            headers.add("X-Usuario-Perfis", perfis);

            try {
                Map<String, Object> usuario = restTemplate.getForObject(
                        SISTEMA_USUARIOS_URL + "/buscar-nome/" + nomeUsuario, Map.class);
                
                if (usuario != null && usuario.get("id") != null) {
                    headers.add("X-Usuario-Logado-Id", usuario.get("id").toString());
                }
            } catch (Exception e) {
                System.err.println("Erro ao obter ID do usuário: " + e.getMessage());
            }
        }

        return new HttpEntity<>(body, headers);
    }

    @GetMapping("/empresa/{empresaId}")
    public ResponseEntity<List<ServicoResDto>> obterServicosPorEmpresa(
            @PathVariable Long empresaId, 
            Authentication authentication) {
            
        HttpEntity<Void> requestEntity = montarRequisicaoComHeaders(null, authentication);

        return restTemplate.exchange(
                SISTEMA_SERVICOS_URL + "/empresa/" + empresaId,
                HttpMethod.GET,
                requestEntity,
                new ParameterizedTypeReference<List<ServicoResDto>>() {}
        );
    }

    @GetMapping("/servicos")
    public ResponseEntity<List<ServicoResDto>> obterServicos(Authentication authentication) {
        HttpEntity<Void> requestEntity = montarRequisicaoComHeaders(null, authentication);

        return restTemplate.exchange(
                SISTEMA_SERVICOS_URL + "/servicos",
                HttpMethod.GET,
                requestEntity,
                new ParameterizedTypeReference<List<ServicoResDto>>() {}
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServicoResDto> obterServico(@PathVariable Long id, Authentication authentication) {
        HttpEntity<Void> requestEntity = montarRequisicaoComHeaders(null, authentication);

        return restTemplate.exchange(
                SISTEMA_SERVICOS_URL + "/" + id,
                HttpMethod.GET,
                requestEntity,
                ServicoResDto.class
        );
    }

    @PostMapping("/cadastrar")
    public ResponseEntity<ServicoResDto> criarServico(@RequestBody ServicoReqDto dtoReq, Authentication authentication) {
        HttpEntity<ServicoReqDto> requestEntity = montarRequisicaoComHeaders(dtoReq, authentication);

        return restTemplate.exchange(
                SISTEMA_SERVICOS_URL + "/cadastrar",
                HttpMethod.POST,
                requestEntity,
                ServicoResDto.class
        );
    }

    @PutMapping("/atualizar/{id}")
    public ResponseEntity<ServicoResDto> atualizarServico(
            @PathVariable Long id, 
            @RequestBody ServicoReqDto dtoReq, 
            Authentication authentication) {
            
        HttpEntity<ServicoReqDto> requestEntity = montarRequisicaoComHeaders(dtoReq, authentication);

        return restTemplate.exchange(
                SISTEMA_SERVICOS_URL + "/atualizar/" + id,
                HttpMethod.PUT,
                requestEntity,
                ServicoResDto.class
        );
    }

    @DeleteMapping("/deletar/{id}")
    public ResponseEntity<Void> excluirServico(@PathVariable Long id, Authentication authentication) {
        HttpEntity<Void> requestEntity = montarRequisicaoComHeaders(null, authentication);

        return restTemplate.exchange(
                SISTEMA_SERVICOS_URL + "/deletar/" + id,
                HttpMethod.DELETE,
                requestEntity,
                Void.class
        );
    }
}