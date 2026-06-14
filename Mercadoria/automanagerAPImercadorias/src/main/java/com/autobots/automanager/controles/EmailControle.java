package com.autobots.automanager.controles;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import com.autobots.automanager.dto.EmailReqDto;
import com.autobots.automanager.dto.EmailResDto;

@RestController
@RequestMapping("/email")
public class EmailControle {

    @Autowired
    private RestTemplate restTemplate;

    private static final String SISTEMA_URL = "http://localhost:8081/email";

    @GetMapping("/{usuarioId}/{emailId}")
    public ResponseEntity<EmailResDto> obterEmail(@PathVariable Long usuarioId, @PathVariable Long emailId) {
        ResponseEntity<EmailResDto> response = restTemplate.exchange(
            SISTEMA_URL + "/" + usuarioId + "/" + emailId,
            HttpMethod.GET,
            null,
            EmailResDto.class
        );
        return response;
    }

    @PostMapping("/cadastrar/{usuarioId}")
    public ResponseEntity<EmailResDto> cadastrarEmail(@PathVariable Long usuarioId, @RequestBody EmailReqDto dtoReq) {
        ResponseEntity<EmailResDto> response = restTemplate.postForEntity(
            SISTEMA_URL + "/cadastrar/" + usuarioId,
            dtoReq,
            EmailResDto.class
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response.getBody());
    }

    @PutMapping("/atualizar/{usuarioId}/{emailId}")
    public ResponseEntity<EmailResDto> atualizarEmail(
            @PathVariable Long usuarioId, 
            @PathVariable Long emailId, 
            @RequestBody EmailReqDto dtoReq) {
        restTemplate.put(SISTEMA_URL + "/atualizar/" + usuarioId + "/" + emailId, dtoReq);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/deletar/{usuarioId}/{emailId}")
    public ResponseEntity<Void> excluirEmail(@PathVariable Long usuarioId, @PathVariable Long emailId) {
        restTemplate.delete(SISTEMA_URL + "/deletar/" + usuarioId + "/" + emailId);
        return ResponseEntity.ok().build();
    }
}