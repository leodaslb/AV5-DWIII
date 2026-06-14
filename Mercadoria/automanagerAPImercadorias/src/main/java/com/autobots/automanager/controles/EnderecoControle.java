package com.autobots.automanager.controles;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import com.autobots.automanager.dto.EnderecoReqDto;
import com.autobots.automanager.dto.EnderecoResDto;

import java.util.List;

@RestController
@RequestMapping("/endereco")
public class EnderecoControle {

    @Autowired
    private RestTemplate restTemplate;

    private static final String SISTEMA_URL = "http://localhost:8081/endereco";

    @GetMapping("/enderecos")
    public ResponseEntity<List<EnderecoResDto>> selecionarTodos() {
        ResponseEntity<List<EnderecoResDto>> response = restTemplate.exchange(
            SISTEMA_URL + "/enderecos",
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<EnderecoResDto>>() {}
        );
        return response;
    }

    @GetMapping("/{id}")
    public ResponseEntity<EnderecoResDto> selecionarEndereco(@PathVariable Long id) {
        ResponseEntity<EnderecoResDto> response = restTemplate.exchange(
            SISTEMA_URL + "/" + id,
            HttpMethod.GET,
            null,
            EnderecoResDto.class
        );
        return response;
    }

    @PostMapping("/cadastro/{clienteId}")
    public ResponseEntity<EnderecoResDto> adicionarEndereco(@PathVariable Long clienteId, @RequestBody EnderecoReqDto dtoReq) {
        ResponseEntity<EnderecoResDto> response = restTemplate.postForEntity(
            SISTEMA_URL + "/cadastro/" + clienteId,
            dtoReq,
            EnderecoResDto.class
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response.getBody());
    }

    @PutMapping("/atualizar/{clienteId}")
    public ResponseEntity<EnderecoResDto> editarEndereco(@PathVariable Long clienteId, @RequestBody EnderecoReqDto dtoReq) {
        restTemplate.put(SISTEMA_URL + "/atualizar/" + clienteId, dtoReq);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/deletar/{clienteId}")
    public ResponseEntity<Void> deletarEndereco(@PathVariable Long clienteId) {
        restTemplate.delete(SISTEMA_URL + "/deletar/" + clienteId);
        return ResponseEntity.ok().build();
    }
}