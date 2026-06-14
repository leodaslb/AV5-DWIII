package com.autobots.automanager.dto;

import java.util.Set;
import com.autobots.automanager.enumeracoes.TipoVeiculo;
import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

@Data
public class VeiculoResDto extends RepresentationModel<VeiculoResDto> {
    private Long id;
    private Long empresaId;
    private TipoVeiculo tipo;
    private String modelo;
    private String placa;
    private Object proprietario; 
 
}