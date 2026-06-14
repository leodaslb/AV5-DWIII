package com.autobots.automanager.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.autobots.automanager.dto.CredencialResDto;
import com.autobots.automanager.dto.UsuarioReqDto;
import com.autobots.automanager.dto.UsuarioResDto;
import com.autobots.automanager.entidades.Usuario;
import com.autobots.automanager.modelos.Perfil;
import com.autobots.automanager.repositorios.UsuarioRepositorio;

@Service
public class UsuarioServico {

    @Autowired
    private UsuarioRepositorio repositorio;

    private StringVerificadorNulo verificador = new StringVerificadorNulo();

    @Autowired
    private EnderecoServico enderecoServico;

    @Autowired
    private TelefoneServico telefoneServico;

    @Autowired
    private DocumentoServico documentoServico;

    @Autowired
    private EmailServico emailServico;

    private Usuario converterUsuario(UsuarioReqDto dto) {
        Usuario usuario = new Usuario();
        usuario.setNome(dto.getNome());
        usuario.setNomeSocial(dto.getNomeSocial());
        usuario.setPerfis(dto.getPerfis());
        usuario.setEmpresaId(dto.getEmpresaId());

        if (dto.getEndereco() != null)
            usuario.setEndereco(enderecoServico.converterEndereco(dto.getEndereco()));

        if (dto.getTelefones() != null)
            usuario.getTelefones().addAll(telefoneServico.converterListaTelefones(dto.getTelefones()));

        if (dto.getDocumentos() != null)
            usuario.getDocumentos().addAll(documentoServico.converterListaDocumentos(dto.getDocumentos()));

        if (dto.getEmails() != null)
            usuario.getEmails().addAll(emailServico.converterListaEmails(dto.getEmails()));

        return usuario;
    }
    
    public Usuario selecionarPorNome(String nomeUsuario) {
        return repositorio.findByCredencialNomeUsuario(nomeUsuario);
    }

    public Usuario criarUsuario(UsuarioReqDto dto) {
        return repositorio.save(converterUsuario(dto));
    }

    public Usuario selecionarPorId(Long id) {
        return repositorio.findById(id).orElse(null);
    }

    public List<Usuario> selecionarTodos() {
        return repositorio.findAll();
    }

    public List<UsuarioResDto> listarClientesPorEmpresa(Long empresaId) {
        List<Usuario> usuariosDaEmpresa = repositorio.findByEmpresaId(empresaId);
        List<Usuario> clientes = usuariosDaEmpresa.stream()
                .filter(u -> u.getPerfis().contains(Perfil.ROLE_CLIENTE))
                .collect(Collectors.toList());
        return toResDtoList(clientes);
    }

    public List<UsuarioResDto> listarFuncionariosPorEmpresa(Long empresaId) {
        List<Usuario> usuariosDaEmpresa = repositorio.findByEmpresaId(empresaId);
        List<Usuario> funcionarios = usuariosDaEmpresa.stream()
                .filter(u -> u.getPerfis().contains(Perfil.ROLE_VENDEDOR) || u.getPerfis().contains(Perfil.ROLE_ADMIN )
            || u.getPerfis().contains(Perfil.ROLE_GERENTE ))
                .collect(Collectors.toList());
        return toResDtoList(funcionarios);
    }

    private void atualizarDados(Usuario usuario, UsuarioReqDto atualizacao) {
        if (atualizacao.getEmpresaId() != null)
            usuario.setEmpresaId(atualizacao.getEmpresaId());
        if (!verificador.verificar(atualizacao.getNome()))
            usuario.setNome(atualizacao.getNome());
        if (!verificador.verificar(atualizacao.getNomeSocial()))
            usuario.setNomeSocial(atualizacao.getNomeSocial());
        if (atualizacao.getPerfis() != null && !atualizacao.getPerfis().isEmpty())
            usuario.setPerfis(atualizacao.getPerfis());
    }

    public Usuario atualizar(Long id, UsuarioReqDto atualizacao) {
        Usuario usuario = repositorio.findById(id).orElse(null);
        if (usuario != null) {
            atualizarDados(usuario, atualizacao);

            if (atualizacao.getEndereco() != null)
                enderecoServico.atualizar(usuario.getEndereco(),
                        enderecoServico.converterEndereco(atualizacao.getEndereco()));

            if (atualizacao.getTelefones() != null)
                telefoneServico.atualizar(usuario.getTelefones(),
                        telefoneServico.converterListaTelefones(atualizacao.getTelefones()));

            if (atualizacao.getDocumentos() != null)
                documentoServico.atualizar(usuario.getDocumentos(),
                        documentoServico.converterListaDocumentos(atualizacao.getDocumentos()));

            if (atualizacao.getEmails() != null)
                emailServico.atualizar(usuario.getEmails(),
                        emailServico.converterListaEmails(atualizacao.getEmails()));

            return repositorio.save(usuario);
        }
        return null;
    }

    public void excluir(Long id) {
        Usuario usuario = repositorio.findById(id).orElse(null);
        if (usuario != null)
            repositorio.delete(usuario);
    }

    public UsuarioResDto toResDto(Usuario usuario) {
        if (usuario == null) return null;
        UsuarioResDto dto = new UsuarioResDto();
        dto.setId(usuario.getId());
        dto.setEmpresaId(usuario.getEmpresaId());
        dto.setNome(usuario.getNome());
        dto.setNomeSocial(usuario.getNomeSocial());
        dto.setPerfis(usuario.getPerfis());

        if (usuario.getEndereco() != null)
            dto.setEndereco(enderecoServico.toResDto(usuario.getEndereco()));

        dto.setTelefones(usuario.getTelefones().stream()
                .map(telefoneServico::toResDto).collect(Collectors.toSet()));

        dto.setDocumentos(usuario.getDocumentos().stream()
                .map(documentoServico::toResDto).collect(Collectors.toSet()));

        dto.setEmails(usuario.getEmails().stream()
                .map(emailServico::toResDto).collect(Collectors.toSet()));
                
        if (usuario.getCredencial() != null) {
            CredencialResDto credencialDto = new CredencialResDto();
            credencialDto.setNomeUsuario(usuario.getCredencial().getNomeUsuario());
            credencialDto.setSenha(usuario.getCredencial().getSenha());
            dto.setCredencial(credencialDto);
        }

        return dto;
    }

    public List<UsuarioResDto> toResDtoList(List<Usuario> usuarios) {
        return usuarios.stream().map(this::toResDto).collect(Collectors.toList());
    }
}