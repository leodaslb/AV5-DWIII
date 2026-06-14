package com.autobots.automanager.dto;

import java.util.Date;
import java.util.Set;
import org.springframework.hateoas.RepresentationModel;
import lombok.Data;

@Data
public class VendaResDto extends RepresentationModel<VendaResDto> {
    private Long id;
    private Date cadastro;
    private String identificacao;
    private Object cliente;       
    private Object funcionario;  
    private Object veiculo;       
    private Long empresaId;
    private Set<Object> mercadorias; 
    private Set<Object> servicos;
}