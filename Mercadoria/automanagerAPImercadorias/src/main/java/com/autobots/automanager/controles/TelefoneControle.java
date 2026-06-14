package com.autobots.automanager.controles;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import com.autobots.automanager.dto.TelefoneReqDto;
import com.autobots.automanager.dto.TelefoneResDto;

@RestController
@RequestMapping("/telefone")
public class TelefoneControle {

    @Autowired
    private RestTemplate restTemplate;

    private static final String SISTEMA_URL = "http://localhost:8081/telefone";

    @GetMapping("/{clienteId}/{telefoneId}")
    public ResponseEntity<TelefoneResDto> selecionarTelefone(@PathVariable Long clienteId, @PathVariable Long telefoneId) {
        ResponseEntity<TelefoneResDto> response = restTemplate.exchange(
            SISTEMA_URL + "/" + clienteId + "/" + telefoneId,
            HttpMethod.GET,
            null,
            TelefoneResDto.class
        );
        return response;
    }

    @PostMapping("/cadastro/{clienteId}")
    public ResponseEntity<TelefoneResDto> adicionarTelefone(@PathVariable Long clienteId, @RequestBody TelefoneReqDto dtoReq) {
        ResponseEntity<TelefoneResDto> response = restTemplate.postForEntity(
            SISTEMA_URL + "/cadastro/" + clienteId,
            dtoReq,
            TelefoneResDto.class
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response.getBody());
    }

    @PutMapping("/atualizar/{clienteId}/{telefoneId}")
    public ResponseEntity<TelefoneResDto> editarTelefone(@PathVariable Long clienteId, @PathVariable Long telefoneId, @RequestBody TelefoneReqDto dtoReq) {
        restTemplate.put(SISTEMA_URL + "/atualizar/" + clienteId + "/" + telefoneId, dtoReq);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/deletar/{clienteId}/{telefoneId}")
    public ResponseEntity<Void> deletarTelefone(@PathVariable Long clienteId, @PathVariable Long telefoneId) {
        restTemplate.delete(SISTEMA_URL + "/deletar/" + clienteId + "/" + telefoneId);
        return ResponseEntity.ok().build();
    }
}