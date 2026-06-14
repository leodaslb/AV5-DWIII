package com.autobots.automanager.controles;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import com.autobots.automanager.dto.MercadoriaReqDto;
import com.autobots.automanager.dto.MercadoriaResDto;

import java.util.List;

@RestController
@RequestMapping("/mercadoria")
public class MercadoriaControle {

    @Autowired
    private RestTemplate restTemplate;

    // Apontando para o microserviço de Catálogo/Estoque na porta 8083
    private static final String SISTEMA_URL = "http://localhost:8083/mercadoria";

    @GetMapping("/mercadorias")
    public ResponseEntity<List<MercadoriaResDto>> obterMercadorias() {
        ResponseEntity<List<MercadoriaResDto>> response = restTemplate.exchange(
            SISTEMA_URL + "/mercadorias",
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<MercadoriaResDto>>() {}
        );
        return response;
    }

    @GetMapping("/{id}")
    public ResponseEntity<MercadoriaResDto> obterMercadoria(@PathVariable Long id) {
        ResponseEntity<MercadoriaResDto> response = restTemplate.exchange(
            SISTEMA_URL + "/" + id,
            HttpMethod.GET,
            null,
            MercadoriaResDto.class
        );
        return response;
    }

    @PostMapping("/cadastrar")
    public ResponseEntity<MercadoriaResDto> criarMercadoria(@RequestBody MercadoriaReqDto dtoReq) {
        ResponseEntity<MercadoriaResDto> response = restTemplate.postForEntity(
            SISTEMA_URL + "/cadastrar",
            dtoReq,
            MercadoriaResDto.class
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response.getBody());
    }

    @PutMapping("/atualizar/{id}")
    public ResponseEntity<MercadoriaResDto> atualizarMercadoria(@PathVariable Long id, @RequestBody MercadoriaReqDto dtoReq) {
        restTemplate.put(SISTEMA_URL + "/atualizar/" + id, dtoReq);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/deletar/{id}")
    public ResponseEntity<Void> excluirMercadoria(@PathVariable Long id) {
        restTemplate.delete(SISTEMA_URL + "/deletar/" + id);
        return ResponseEntity.ok().build();
    }
}