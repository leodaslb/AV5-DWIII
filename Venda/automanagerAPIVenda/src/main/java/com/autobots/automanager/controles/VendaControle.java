package com.autobots.automanager.controles;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import com.autobots.automanager.dto.VendaReqDto;
import com.autobots.automanager.dto.VendaResDto;
import com.autobots.automanager.dto.UsuarioResDto;

@RestController
@RequestMapping("/venda")
public class VendaControle {

    @Autowired
    private RestTemplate restTemplate;

    private static final String SISTEMA_VENDAS_URL = "http://localhost:8085/venda";
    private static final String SISTEMA_USUARIOS_URL = "http://localhost:8081/usuario";

    private <T> HttpEntity<T> montarRequisicaoComHeaders(T body, Principal principal) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        if (principal != null) {
            try {
                UsuarioResDto usuarioLogado = restTemplate.getForObject(
                        SISTEMA_USUARIOS_URL + "/porNome/" + principal.getName(), 
                        UsuarioResDto.class
                );
                
                if (usuarioLogado != null) {
                    headers.add("X-Usuario-Logado-Id", usuarioLogado.getId().toString());
                    
                    String perfis = usuarioLogado.getPerfis().stream()
                            .map(Object::toString)
                            .collect(Collectors.joining(","));
                    
                    headers.add("X-Usuario-Perfis", perfis);
                }
            } catch (Exception e) {
                System.err.println("Erro ao obter ID do usuário: " + e.getMessage());
            }
        }

        return new HttpEntity<>(body, headers);
    }

    @GetMapping("/empresa/{empresaId}")
    public ResponseEntity<List<VendaResDto>> listarVendasDaEmpresaPorPeriodo(
            @PathVariable Long empresaId,
            @RequestParam String inicio,
            @RequestParam String fim,
            Principal principal) {
            
        HttpEntity<Void> requestEntity = montarRequisicaoComHeaders(null, principal);
        String urlRelatorio = SISTEMA_VENDAS_URL + "/empresa/" + empresaId + "?inicio=" + inicio + "&fim=" + fim;

        return restTemplate.exchange(
                urlRelatorio,
                HttpMethod.GET,
                requestEntity,
                new ParameterizedTypeReference<List<VendaResDto>>() {}
        );
    }

    @GetMapping("/vendas")
    public ResponseEntity<List<VendaResDto>> obterVendas(Principal principal) {
        HttpEntity<Void> requestEntity = montarRequisicaoComHeaders(null, principal);

        return restTemplate.exchange(
                SISTEMA_VENDAS_URL + "/vendas",
                HttpMethod.GET,
                requestEntity,
                new ParameterizedTypeReference<List<VendaResDto>>() {}
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<VendaResDto> obterVenda(@PathVariable Long id, Principal principal) {
        HttpEntity<Void> requestEntity = montarRequisicaoComHeaders(null, principal);

        return restTemplate.exchange(
                SISTEMA_VENDAS_URL + "/" + id,
                HttpMethod.GET,
                requestEntity,
                VendaResDto.class
        );
    }

    @PostMapping("/cadastrar")
    public ResponseEntity<VendaResDto> criarVenda(@RequestBody VendaReqDto dtoReq, Principal principal) {
        HttpEntity<VendaReqDto> requestEntity = montarRequisicaoComHeaders(dtoReq, principal);

        return restTemplate.exchange(
                SISTEMA_VENDAS_URL + "/cadastrar",
                HttpMethod.POST,
                requestEntity,
                VendaResDto.class
        );
    }

    @PutMapping("/atualizar/{id}")
    public ResponseEntity<VendaResDto> atualizarVenda(@PathVariable Long id, @RequestBody VendaReqDto dtoReq, Principal principal) {
        HttpEntity<VendaReqDto> requestEntity = montarRequisicaoComHeaders(dtoReq, principal);

        return restTemplate.exchange(
                SISTEMA_VENDAS_URL + "/atualizar/" + id,
                HttpMethod.PUT,
                requestEntity,
                VendaResDto.class
        );
    }

    @DeleteMapping("/deletar/{id}")
    public ResponseEntity<Void> excluirVenda(@PathVariable Long id, Principal principal) {
        HttpEntity<Void> requestEntity = montarRequisicaoComHeaders(null, principal);

        return restTemplate.exchange(
                SISTEMA_VENDAS_URL + "/deletar/" + id,
                HttpMethod.DELETE,
                requestEntity,
                Void.class
        );
    }
}