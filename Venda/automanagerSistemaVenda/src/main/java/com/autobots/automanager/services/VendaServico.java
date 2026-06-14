package com.autobots.automanager.services;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.autobots.automanager.dto.VendaReqDto;
import com.autobots.automanager.dto.VendaResDto;
import com.autobots.automanager.entidades.Venda;
import com.autobots.automanager.repositorios.VendaRepositorio;

@Service
public class VendaServico {

    @Autowired
    private VendaRepositorio vendaRepositorio;

    @Autowired
    private RestTemplate restTemplate;

    private static final String URL_USUARIOS = "http://localhost:8081/usuario";
    private static final String URL_MERCADORIAS = "http://localhost:8083/mercadoria";
    private static final String URL_SERVICOS = "http://localhost:8083/servico";
    private static final String URL_VEICULOS = "http://localhost:8087/veiculo";

    private StringVerificadorNulo verificador = new StringVerificadorNulo();

    private Venda converterVenda(VendaReqDto dto) {
        Venda venda = new Venda();
        venda.setIdentificacao(dto.getIdentificacao());
        venda.setCadastro(Calendar.getInstance().getTime());
        venda.setEmpresaId(dto.getEmpresaId());

        if (dto.getClienteId() != null) {
            try {
                restTemplate.getForEntity(URL_USUARIOS + "/" + dto.getClienteId(), Object.class);
                venda.setClienteId(dto.getClienteId());
            } catch (RestClientException e) {
                throw new RuntimeException("Cliente ID " + dto.getClienteId() + " não encontrado.");
            }
        }

        if (dto.getFuncionarioId() != null) {
            try {
                restTemplate.getForEntity(URL_USUARIOS + "/" + dto.getFuncionarioId(), Object.class);
                venda.setFuncionarioId(dto.getFuncionarioId());
            } catch (RestClientException e) {
                throw new RuntimeException("Funcionário ID " + dto.getFuncionarioId() + " não encontrado.");
            }
        }

        if (dto.getVeiculoId() != null) {
            try {
                restTemplate.getForEntity(URL_VEICULOS + "/" + dto.getVeiculoId(), Object.class);
                venda.setVeiculoId(dto.getVeiculoId());
            } catch (RestClientException e) {
                throw new RuntimeException("Veículo ID " + dto.getVeiculoId() + " não encontrado.");
            }
        }

        if (dto.getMercadoriaIds() != null && !dto.getMercadoriaIds().isEmpty()) {
            for (Long idMercadoria : dto.getMercadoriaIds()) {
                try {
                    restTemplate.getForEntity(URL_MERCADORIAS + "/" + idMercadoria, Object.class);
                } catch (RestClientException e) {
                    throw new RuntimeException("Mercadoria ID " + idMercadoria + " não encontrada.");
                }
            }
            venda.setMercadoriaIds(dto.getMercadoriaIds());
        }

        if (dto.getServicoIds() != null && !dto.getServicoIds().isEmpty()) {
            for (Long idServico : dto.getServicoIds()) {
                try {
                    restTemplate.getForEntity(URL_SERVICOS + "/" + idServico, Object.class);
                } catch (RestClientException e) {
                    throw new RuntimeException("Serviço ID " + idServico + " não encontrado.");
                }
            }
            venda.setServicoIds(dto.getServicoIds());
        }

        return venda;
    }

    public Venda criarVenda(VendaReqDto dto) {
        return vendaRepositorio.save(converterVenda(dto));
    }

    public List<Venda> selecionarTodos() {
        return vendaRepositorio.findAll();
    }

    public List<VendaResDto> listarPorEmpresaEPeriodo(Long empresaId, Date dataInicial, Date dataFinal) {
        List<Venda> vendas = vendaRepositorio.findByEmpresaIdAndCadastroBetween(empresaId, dataInicial, dataFinal);
        return toResDtoList(vendas);
    }

    public Venda selecionarPorId(Long id) {
        return vendaRepositorio.findById(id).orElse(null);
    }

    private void atualizarDados(Venda venda, VendaReqDto atualizacao) {
        if (!verificador.verificar(atualizacao.getIdentificacao())) {
            venda.setIdentificacao(atualizacao.getIdentificacao());
        }

        if (atualizacao.getEmpresaId() != null) {
            venda.setEmpresaId(atualizacao.getEmpresaId());
        }

        if (atualizacao.getClienteId() != null) {
            try {
                restTemplate.getForEntity(URL_USUARIOS + "/" + atualizacao.getClienteId(), Object.class);
                venda.setClienteId(atualizacao.getClienteId());
            } catch (RestClientException e) {
                throw new RuntimeException("Cliente ID " + atualizacao.getClienteId() + " não encontrado.");
            }
        }

        if (atualizacao.getFuncionarioId() != null) {
            try {
                restTemplate.getForEntity(URL_USUARIOS + "/" + atualizacao.getFuncionarioId(), Object.class);
                venda.setFuncionarioId(atualizacao.getFuncionarioId());
            } catch (RestClientException e) {
                throw new RuntimeException("Funcionário ID " + atualizacao.getFuncionarioId() + " não encontrado.");
            }
        }

        if (atualizacao.getVeiculoId() != null) {
            try {
                restTemplate.getForEntity(URL_VEICULOS + "/" + atualizacao.getVeiculoId(), Object.class);
                venda.setVeiculoId(atualizacao.getVeiculoId());
            } catch (RestClientException e) {
                throw new RuntimeException("Veículo ID " + atualizacao.getVeiculoId() + " não encontrado.");
            }
        }

        if (atualizacao.getMercadoriaIds() != null && !atualizacao.getMercadoriaIds().isEmpty()) {
            venda.setMercadoriaIds(atualizacao.getMercadoriaIds());
        }

        if (atualizacao.getServicoIds() != null && !atualizacao.getServicoIds().isEmpty()) {
            venda.setServicoIds(atualizacao.getServicoIds());
        }
    }

    public Venda atualizar(Long id, VendaReqDto atualizacao) {
        Venda venda = vendaRepositorio.findById(id).orElse(null);
        if (venda != null) {
            atualizarDados(venda, atualizacao);
            return vendaRepositorio.save(venda);
        }
        return null;
    }

    public void excluir(Long id) {
        Venda venda = vendaRepositorio.findById(id).orElse(null);
        if (venda != null) {
            vendaRepositorio.delete(venda);
        }
    }

    public VendaResDto toResDto(Venda venda) {
        if (venda == null) return null;
        VendaResDto dto = new VendaResDto();
        dto.setId(venda.getId());
        dto.setCadastro(venda.getCadastro());
        dto.setIdentificacao(venda.getIdentificacao());
        dto.setEmpresaId(venda.getEmpresaId());

        if (venda.getClienteId() != null) {
            try {
                Object clienteCompleto = restTemplate.getForObject(URL_USUARIOS + "/" + venda.getClienteId(), Object.class);
                dto.setCliente(clienteCompleto); 
            } catch (Exception e) {
                dto.setCliente(null);
            }
        }

        if (venda.getFuncionarioId() != null) {
            try {
                Object funcionarioCompleto = restTemplate.getForObject(URL_USUARIOS + "/" + venda.getFuncionarioId(), Object.class);
                dto.setFuncionario(funcionarioCompleto); 
            } catch (Exception e) {
                dto.setFuncionario(null);
            }
        }

        if (venda.getVeiculoId() != null) {
            try {
                Object veiculoCompleto = restTemplate.getForObject(URL_VEICULOS + "/" + venda.getVeiculoId(), Object.class);
                dto.setVeiculo(veiculoCompleto);
            } catch (Exception e) {
                dto.setVeiculo(null);
            }
        }

        if (venda.getMercadoriaIds() != null && !venda.getMercadoriaIds().isEmpty()) {
            Set<Object> mercadorias = venda.getMercadoriaIds().stream().map(id -> {
                try {
                    return restTemplate.getForObject(URL_MERCADORIAS + "/" + id, Object.class);
                } catch (Exception e) {
                    return null;
                }
            }).collect(Collectors.toSet());
            dto.setMercadorias(mercadorias);
        }

        if (venda.getServicoIds() != null && !venda.getServicoIds().isEmpty()) {
            Set<Object> servicos = venda.getServicoIds().stream().map(id -> {
                try {
                    return restTemplate.getForObject(URL_SERVICOS + "/" + id, Object.class);
                } catch (Exception e) {
                    return null;
                }
            }).collect(Collectors.toSet());
            dto.setServicos(servicos);
        }

        return dto;
    }

    public List<VendaResDto> toResDtoList(List<Venda> vendas) {
        return vendas.stream().map(this::toResDto).collect(Collectors.toList());
    }
}