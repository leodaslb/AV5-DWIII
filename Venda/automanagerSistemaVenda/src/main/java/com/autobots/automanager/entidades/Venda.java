package com.autobots.automanager.entidades;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(exclude = { "mercadoriaIds", "servicoIds" }) 
@Entity
public class Venda {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Date cadastro;
    
    @Column(nullable = false, unique = true)
    private String identificacao;

   

    @Column(name = "cliente_id")
    private Long clienteId;

    @Column(name = "funcionario_id")
    private Long funcionarioId;

    @Column(name = "veiculo_id")
    private Long veiculoId;

	@Column(name = "empresa_id", nullable = false)
    private Long empresaId;
  
    @ElementCollection
    @Column(name = "mercadoria_id")
    private Set<Long> mercadoriaIds = new HashSet<>();

    @ElementCollection
    @Column(name = "servico_id")
    private Set<Long> servicoIds = new HashSet<>();
}