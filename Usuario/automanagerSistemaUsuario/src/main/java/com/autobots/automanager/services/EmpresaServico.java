package com.autobots.automanager.services;

import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.autobots.automanager.dto.EmpresaReqDto;
import com.autobots.automanager.dto.EmpresaResDto;
import com.autobots.automanager.entidades.Empresa;

import com.autobots.automanager.entidades.Usuario;

import com.autobots.automanager.repositorios.EmpresaRepositorio;

import com.autobots.automanager.repositorios.UsuarioRepositorio;


@Service
public class EmpresaServico {

    @Autowired
    private EmpresaRepositorio repositorio;

    @Autowired
    private EnderecoServico enderecoServico;

    @Autowired
    private TelefoneServico telefoneServico;

    @Autowired
    private UsuarioServico usuarioServico;

    
    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

  

    
    private StringVerificadorNulo verificador = new StringVerificadorNulo();

    private Empresa converterEmpresa(EmpresaReqDto dto) {
        Empresa empresa = new Empresa();
        empresa.setRazaoSocial(dto.getRazaoSocial());
        empresa.setNomeFantasia(dto.getNomeFantasia());
        empresa.setCadastro(Calendar.getInstance().getTime());

        if (dto.getEndereco() != null)
            empresa.setEndereco(enderecoServico.converterEndereco(dto.getEndereco()));

        
        if (dto.getTelefones() != null)
            empresa.getTelefones().addAll(telefoneServico.converterListaTelefones(dto.getTelefones()));

        return empresa;
    }

    public Empresa criarEmpresa(EmpresaReqDto dto) {
        return repositorio.save(converterEmpresa(dto));
    }

    public List<Empresa> selecionarTodos() {
        return repositorio.findAll();
    }

    public Empresa selecionarPorId(Long id) {
        return repositorio.findById(id).orElse(null);
    }

    private void atualizarDados(Empresa empresa, EmpresaReqDto atualizacao) {
        if (!verificador.verificar(atualizacao.getNomeFantasia()))
            empresa.setNomeFantasia(atualizacao.getNomeFantasia());
        if (!verificador.verificar(atualizacao.getRazaoSocial()))
            empresa.setRazaoSocial(atualizacao.getRazaoSocial());
    }

    public Empresa atualizar(Long id, EmpresaReqDto atualizacao) {
        Empresa empresa = repositorio.findById(id).orElse(null);
        if (empresa != null) {
            atualizarDados(empresa, atualizacao);
            if (atualizacao.getEndereco() != null)
                enderecoServico.atualizar(empresa.getEndereco(),
                        enderecoServico.converterEndereco(atualizacao.getEndereco()));
            if (atualizacao.getTelefones() != null)
                telefoneServico.atualizar(empresa.getTelefones(),
                        telefoneServico.converterListaTelefones(atualizacao.getTelefones()));
            return repositorio.save(empresa);
        }
        return null;
    }

    public void excluir(Long id) {
        Empresa empresa = repositorio.findById(id).orElse(null);
        if (empresa != null)
            repositorio.delete(empresa);
    }

    // --- Associações ---

    public void associarUsuario(Long empresaId, Long usuarioId) {
        Empresa empresa = repositorio.findById(empresaId)
                .orElseThrow(() -> new RuntimeException("Empresa não encontrada"));
        Usuario usuario = usuarioRepositorio.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario não encontrado"));
        empresa.getUsuarios().add(usuario);
        repositorio.save(empresa);
    }

    public void desassociarUsuario(Long empresaId, Long usuarioId) {
        Empresa empresa = repositorio.findById(empresaId)
                .orElseThrow(() -> new RuntimeException("Empresa não encontrada"));
        empresa.getUsuarios().removeIf(u -> u.getId().equals(usuarioId));
        repositorio.save(empresa);
    }

   
   

    

    

   


    // --- toResDto ---

    public EmpresaResDto toResDto(Empresa empresa) {
        if (empresa == null) return null;
        EmpresaResDto dto = new EmpresaResDto();
        dto.setId(empresa.getId());
        dto.setRazaoSocial(empresa.getRazaoSocial());
        dto.setNomeFantasia(empresa.getNomeFantasia());
        dto.setCadastro(empresa.getCadastro());

        if (empresa.getEndereco() != null)
            dto.setEndereco(enderecoServico.toResDto(empresa.getEndereco()));

        dto.setTelefones(empresa.getTelefones().stream()
                .map(telefoneServico::toResDto).collect(Collectors.toSet()));

        dto.setUsuarios(empresa.getUsuarios().stream()
                .map(usuarioServico::toResDto).collect(Collectors.toSet()));

        return dto;
    }

    public List<EmpresaResDto> toResDtoList(List<Empresa> empresas) {
        return empresas.stream().map(this::toResDto).collect(Collectors.toList());
    }
}
