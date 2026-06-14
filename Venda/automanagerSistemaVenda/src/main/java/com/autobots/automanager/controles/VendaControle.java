package com.autobots.automanager.controles;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.autobots.automanager.dto.VendaReqDto;
import com.autobots.automanager.dto.VendaResDto;
import com.autobots.automanager.entidades.Venda;

import com.autobots.automanager.services.VendaServico;

@RestController
@RequestMapping("/venda")
public class VendaControle {

    @Autowired
    private VendaServico vendaServico;



    @GetMapping("/empresa/{empresaId}")
    public ResponseEntity<List<VendaResDto>> listarVendasDaEmpresaPorPeriodo(
            @PathVariable Long empresaId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date inicio,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date fim) {
            
        List<VendaResDto> dto = vendaServico.listarPorEmpresaEPeriodo(empresaId, inicio, fim);
        
        if (dto.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        
       
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/vendas")
    public ResponseEntity<List<VendaResDto>> obterVendas() {
        List<Venda> vendas = vendaServico.selecionarTodos();
        if (vendas.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        List<VendaResDto> dto = vendaServico.toResDtoList(vendas);
       
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VendaResDto> obterVenda(
            @PathVariable Long id,
            @RequestHeader("X-Usuario-Logado-Id") Long idUsuarioLogado,
            @RequestHeader("X-Usuario-Perfis") String perfisUsuario) {
            
        Venda venda = vendaServico.selecionarPorId(id);
        if (venda == null) {
            return ResponseEntity.notFound().build();
        }

        boolean isVendedor = perfisUsuario.contains("ROLE_VENDEDOR");
        boolean isCliente = perfisUsuario.contains("ROLE_CLIENTE");

        if (isVendedor && !idUsuarioLogado.equals(venda.getFuncionarioId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        if (isCliente && !idUsuarioLogado.equals(venda.getClienteId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        VendaResDto dto = vendaServico.toResDto(venda);
        
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/cadastrar")
    public ResponseEntity<VendaResDto> criarVenda(
            @RequestBody VendaReqDto dtoReq,
            @RequestHeader("X-Usuario-Perfis") String perfisUsuario) {
            
        if (perfisUsuario.contains("ROLE_CLIENTE")) {
             return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        Venda venda = vendaServico.criarVenda(dtoReq);
        VendaResDto dtoRes = vendaServico.toResDto(venda);
       
        return ResponseEntity.status(HttpStatus.CREATED).body(dtoRes);
    }

    @PutMapping("/atualizar/{id}")
    public ResponseEntity<VendaResDto> atualizarVenda(@PathVariable Long id, @RequestBody VendaReqDto dtoReq) {
        Venda venda = vendaServico.atualizar(id, dtoReq);
        if (venda == null) {
            return ResponseEntity.notFound().build();
        }
        VendaResDto dtoRes = vendaServico.toResDto(venda);
        
        return ResponseEntity.ok(dtoRes);
    }

    @DeleteMapping("/deletar/{id}")
    public ResponseEntity<Void> excluirVenda(@PathVariable Long id) {
        vendaServico.excluir(id);
        return ResponseEntity.ok().build();
    }
}