package com.autobots.automanager.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.autobots.automanager.dto.VeiculoReqDto;
import com.autobots.automanager.dto.VeiculoResDto;
import com.autobots.automanager.entidades.Veiculo;
import com.autobots.automanager.repositorios.VeiculoRepositorio;

@Service
public class VeiculoServico {

    @Autowired
    private VeiculoRepositorio veiculoRepositorio;

    @Autowired
    private RestTemplate restTemplate;

   
    private static final String URL_USUARIOS = "http://localhost:8081/usuario";

    private StringVerificadorNulo verificador = new StringVerificadorNulo();

    private Veiculo converterVeiculo(VeiculoReqDto dto) {
        Veiculo veiculo = new Veiculo();
        veiculo.setModelo(dto.getModelo());
        veiculo.setPlaca(dto.getPlaca());
        veiculo.setTipo(dto.getTipo());
        veiculo.setEmpresaId(dto.getEmpresaId());

        
        if (dto.getProprietarioId() != null) {
            try {
                restTemplate.getForEntity(URL_USUARIOS + "/" + dto.getProprietarioId(), Object.class);
                veiculo.setProprietarioId(dto.getProprietarioId());
            } catch (HttpClientErrorException.NotFound e) {
                throw new RuntimeException("Proprietário ID " + dto.getProprietarioId() + " não encontrado no sistema de usuários.");
            }
        }
        return veiculo;
    }

    public Veiculo criarVeiculo(VeiculoReqDto dto) {
        Veiculo veiculo = converterVeiculo(dto);
        return veiculoRepositorio.save(veiculo);
    }

    public List<Veiculo> selecionarTodos() {
        return veiculoRepositorio.findAll();
    }

    public Veiculo selecionarPorId(Long id) {
        return veiculoRepositorio.findById(id).orElse(null);
    }

   
    public List<VeiculoResDto> listarPorEmpresa(Long empresaId) {
        List<Veiculo> veiculos = veiculoRepositorio.findByEmpresaId(empresaId);
        return toResDtoList(veiculos);
    }

    private void atualizarDados(Veiculo veiculo, VeiculoReqDto atualizacao) {
        if (!verificador.verificar(atualizacao.getModelo())) {
            veiculo.setModelo(atualizacao.getModelo());
        }
        if (!verificador.verificar(atualizacao.getPlaca())) {
            veiculo.setPlaca(atualizacao.getPlaca());
        }
        if (atualizacao.getTipo() != null) {
            veiculo.setTipo(atualizacao.getTipo()); 
        }
        if (atualizacao.getEmpresaId() != null) {
            veiculo.setEmpresaId(atualizacao.getEmpresaId());
        }
        if (atualizacao.getProprietarioId() != null) {
           
            try {
                restTemplate.getForEntity(URL_USUARIOS + "/" + atualizacao.getProprietarioId(), Object.class);
                veiculo.setProprietarioId(atualizacao.getProprietarioId());
            } catch (HttpClientErrorException.NotFound e) {
                throw new RuntimeException("Proprietário ID " + atualizacao.getProprietarioId() + " não encontrado.");
            }
        }
    }

    public Veiculo atualizar(Long id, VeiculoReqDto atualizacao) {
        Veiculo veiculo = veiculoRepositorio.findById(id).orElse(null);
        if (veiculo != null) {
            atualizarDados(veiculo, atualizacao);
            return veiculoRepositorio.save(veiculo);
        }
        return null;
    }

    public void excluir(Long id) {
        Veiculo veiculo = veiculoRepositorio.findById(id).orElse(null);
        if (veiculo != null) {
            veiculoRepositorio.delete(veiculo);
        }
    }

  
    public VeiculoResDto toResDto(Veiculo veiculo) {
        if (veiculo == null) return null;
        VeiculoResDto dto = new VeiculoResDto();
        dto.setId(veiculo.getId());
        dto.setEmpresaId(veiculo.getEmpresaId());
        dto.setModelo(veiculo.getModelo());
        dto.setPlaca(veiculo.getPlaca());
        dto.setTipo(veiculo.getTipo());
        
       
        if (veiculo.getProprietarioId() != null) {
            try {
                Object proprietarioCompleto = restTemplate.getForObject(URL_USUARIOS + "/" + veiculo.getProprietarioId(), Object.class);
                dto.setProprietario(proprietarioCompleto);
            } catch (Exception e) {
             
                dto.setProprietario(null);
            }
        }
        return dto;
    }

    public List<VeiculoResDto> toResDtoList(List<Veiculo> veiculos) {
        return veiculos.stream().map(this::toResDto).collect(Collectors.toList());
    }
}