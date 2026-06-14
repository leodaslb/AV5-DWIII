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

import com.autobots.automanager.dto.VeiculoReqDto;
import com.autobots.automanager.dto.VeiculoResDto;

@RestController
@RequestMapping("/veiculo")
public class VeiculoControle {

    @Autowired
    private RestTemplate restTemplate;

    // Endereços dos microsserviços de Sistema internos
    private static final String SISTEMA_VEICULOS_URL = "http://localhost:8087/veiculo";
    private static final String SISTEMA_USUARIOS_URL = "http://localhost:8081/usuario";

    private <T> HttpEntity<T> montarRequisicaoComHeaders(T body, Authentication authentication) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        if (authentication != null) {
            String nomeUsuario = authentication.getName();
            
            // Transforma os perfis/roles do Spring Security numa String separada por vírgula
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
                System.err.println("Erro ao obter ID do usuário autenticado: " + e.getMessage());
            }
        }

        return new HttpEntity<>(body, headers);
    }


    @GetMapping("/empresa/{empresaId}")
    public ResponseEntity<List<VeiculoResDto>> obterVeiculosPorEmpresa(
            @PathVariable Long empresaId, 
            Authentication authentication) {
            
        HttpEntity<Void> requestEntity = montarRequisicaoComHeaders(null, authentication);

        return restTemplate.exchange(
                SISTEMA_VEICULOS_URL + "/empresa/" + empresaId,
                HttpMethod.GET,
                requestEntity,
                new ParameterizedTypeReference<List<VeiculoResDto>>() {}
        );
    }

    @GetMapping("/veiculos")
    public ResponseEntity<List<VeiculoResDto>> obterVeiculos(Authentication authentication) {
        HttpEntity<Void> requestEntity = montarRequisicaoComHeaders(null, authentication);

        return restTemplate.exchange(
                SISTEMA_VEICULOS_URL + "/veiculos",
                HttpMethod.GET,
                requestEntity,
                new ParameterizedTypeReference<List<VeiculoResDto>>() {}
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<VeiculoResDto> obterVeiculo(@PathVariable Long id, Authentication authentication) {
        HttpEntity<Void> requestEntity = montarRequisicaoComHeaders(null, authentication);

        return restTemplate.exchange(
                SISTEMA_VEICULOS_URL + "/" + id,
                HttpMethod.GET,
                requestEntity,
                VeiculoResDto.class
        );
    }

    @PostMapping("/cadastrar")
    public ResponseEntity<VeiculoResDto> criarVeiculo(@RequestBody VeiculoReqDto dtoReq, Authentication authentication) {
        HttpEntity<VeiculoReqDto> requestEntity = montarRequisicaoComHeaders(dtoReq, authentication);

        return restTemplate.exchange(
                SISTEMA_VEICULOS_URL + "/cadastrar",
                HttpMethod.POST,
                requestEntity,
                VeiculoResDto.class
        );
    }

    @PutMapping("/atualizar/{id}")
    public ResponseEntity<VeiculoResDto> atualizarVeiculo(
            @PathVariable Long id, 
            @RequestBody VeiculoReqDto dtoReq, 
            Authentication authentication) {
            
        HttpEntity<VeiculoReqDto> requestEntity = montarRequisicaoComHeaders(dtoReq, authentication);

        return restTemplate.exchange(
                SISTEMA_VEICULOS_URL + "/atualizar/" + id,
                HttpMethod.PUT,
                requestEntity,
                VeiculoResDto.class
        );
    }

    @DeleteMapping("/deletar/{id}")
    public ResponseEntity<Void> excluirVeiculo(@PathVariable Long id, Authentication authentication) {
        HttpEntity<Void> requestEntity = montarRequisicaoComHeaders(null, authentication);

        return restTemplate.exchange(
                SISTEMA_VEICULOS_URL + "/deletar/" + id,
                HttpMethod.DELETE,
                requestEntity,
                Void.class
        );
    }
}