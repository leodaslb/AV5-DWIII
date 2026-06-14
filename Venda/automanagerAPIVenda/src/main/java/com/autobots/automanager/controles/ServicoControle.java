package com.autobots.automanager.controles;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import com.autobots.automanager.dto.ServicoReqDto;
import com.autobots.automanager.dto.ServicoResDto;

import java.util.List;

@RestController
@RequestMapping("/servico")
public class ServicoControle {

    @Autowired
    private RestTemplate restTemplate;

    // Apontando para o microserviço de Catálogo/Estoque na porta 8083
    private static final String SISTEMA_URL = "http://localhost:8083/servico";

    @GetMapping("/servicos")
    public ResponseEntity<List<ServicoResDto>> obterServicos() {
        ResponseEntity<List<ServicoResDto>> response = restTemplate.exchange(
            SISTEMA_URL + "/servicos",
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<ServicoResDto>>() {}
        );
        return response;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServicoResDto> obterServico(@PathVariable Long id) {
        ResponseEntity<ServicoResDto> response = restTemplate.exchange(
            SISTEMA_URL + "/" + id,
            HttpMethod.GET,
            null,
            ServicoResDto.class
        );
        return response;
    }

    @PostMapping("/cadastrar")
    public ResponseEntity<ServicoResDto> criarServico(@RequestBody ServicoReqDto dtoReq) {
        ResponseEntity<ServicoResDto> response = restTemplate.postForEntity(
            SISTEMA_URL + "/cadastrar",
            dtoReq,
            ServicoResDto.class
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response.getBody());
    }

    @PutMapping("/atualizar/{id}")
    public ResponseEntity<Void> atualizarServico(@PathVariable Long id, @RequestBody ServicoReqDto dtoReq) {
        restTemplate.put(SISTEMA_URL + "/atualizar/" + id, dtoReq);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/deletar/{id}")
    public ResponseEntity<Void> excluirServico(@PathVariable Long id) {
        restTemplate.delete(SISTEMA_URL + "/deletar/" + id);
        return ResponseEntity.ok().build();
    }
}