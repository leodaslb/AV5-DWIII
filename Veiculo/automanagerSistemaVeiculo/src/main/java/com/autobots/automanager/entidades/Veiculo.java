package com.autobots.automanager.entidades;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.autobots.automanager.enumeracoes.TipoVeiculo;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(exclude = { "vendaIds" }) 
@Entity
public class Veiculo {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private TipoVeiculo tipo;
    
    @Column(nullable = false)
    private String modelo;
    
    @Column(nullable = false, unique = true) 
    private String placa;

    @Column(name = "empresa_id", nullable = false)
    private Long empresaId;

    @Column(name = "proprietario_id", nullable = false)
    private Long proprietarioId;

    @ElementCollection
    @Column(name = "venda_id")
    private Set<Long> vendaIds = new HashSet<>();
}