package com.autobots.automanager.controles;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import com.autobots.automanager.dto.DocumentoReqDto;
import com.autobots.automanager.dto.DocumentoResDto;

import java.util.List;

@RestController
@RequestMapping("/documento")
public class DocumentoControle {

    @Autowired
    private RestTemplate restTemplate;

    // Supondo que o endpoint base do microserviço de documentos siga o mesmo padrão
    private static final String SISTEMA_URL = "http://localhost:8081/documento";

    @GetMapping("/documentos")
    public ResponseEntity<List<DocumentoResDto>> selecionarTodos() {
        ResponseEntity<List<DocumentoResDto>> response = restTemplate.exchange(
            SISTEMA_URL + "/documentos",
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<DocumentoResDto>>() {}
        );
        return response;
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<DocumentoResDto> selecionarPorId(@PathVariable Long id) {
        ResponseEntity<DocumentoResDto> response = restTemplate.exchange(
            SISTEMA_URL + "/" + id,
            HttpMethod.GET,
            null,
            DocumentoResDto.class
        );
        return response;
    }

    @PostMapping("/cadastrar/{clienteId}")
    public ResponseEntity<DocumentoResDto> cadastrar(@PathVariable Long clienteId, @RequestBody DocumentoReqDto dtoReq) {
        ResponseEntity<DocumentoResDto> response = restTemplate.postForEntity(
            SISTEMA_URL + "/cadastrar/" + clienteId,
            dtoReq,
            DocumentoResDto.class
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response.getBody());
    }

    @PutMapping("/atualizar/{clienteId}/{documentoId}")
    public ResponseEntity<DocumentoResDto> atualizarDocumento(@PathVariable Long clienteId,
                                              @PathVariable Long documentoId,
                                              @RequestBody DocumentoReqDto dtoReq) {
        restTemplate.put(SISTEMA_URL + "/atualizar/" + clienteId + "/" + documentoId, dtoReq);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/deletar/{clienteId}/{documentoId}")
    public ResponseEntity<Void> deletar(@PathVariable Long clienteId, @PathVariable Long documentoId) {
        restTemplate.delete(SISTEMA_URL + "/deletar/" + clienteId + "/" + documentoId);
        // Alterei o retorno para ResponseEntity<Void> para manter o padrão do seu UsuarioControle
        return ResponseEntity.ok().build(); 
    }
}