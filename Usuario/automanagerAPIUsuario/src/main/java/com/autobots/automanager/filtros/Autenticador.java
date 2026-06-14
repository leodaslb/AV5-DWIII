package com.autobots.automanager.filtros;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.autobots.automanager.dto.CredencialResDto;
import com.autobots.automanager.dto.LoginReqDto;
import com.autobots.automanager.jwt.ProvedorJwt;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Autenticador extends UsernamePasswordAuthenticationFilter {

    private AuthenticationManager gerenciadorAutenticacao;
    private ProvedorJwt provedorJwt;

    public Autenticador(AuthenticationManager gerenciadorAutenticacao, ProvedorJwt provedorJwt) {
        this.gerenciadorAutenticacao = gerenciadorAutenticacao;
        this.provedorJwt = provedorJwt;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        LoginReqDto credencial = null;
        try {
            credencial = new ObjectMapper().readValue(request.getInputStream(), LoginReqDto.class);
        } catch (IOException e) {
            credencial = new LoginReqDto();
            credencial.setNomeUsuario("");
            credencial.setSenha("");
        }
        UsernamePasswordAuthenticationToken dadosAutenticacao = new UsernamePasswordAuthenticationToken(
                credencial.getNomeUsuario(), credencial.getSenha(), new ArrayList<>());
        Authentication autenticacao = gerenciadorAutenticacao.authenticate(dadosAutenticacao);
        return autenticacao;
    }

    @Override
        protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                Authentication autenticacao) throws IOException, ServletException {
            
            UserDetails usuario = (UserDetails) autenticacao.getPrincipal();
            String nomeUsuario = usuario.getUsername();
    
            // 1. Extrai os perfis (que vieram do seu Enum) como uma lista de Strings puras
            List<String> perfisString = usuario.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
    
            // 2. Passa a lista de Strings para o provedor gerar o JWT
            String jwt = provedorJwt.proverJwt(nomeUsuario, perfisString);
            
            response.addHeader("Authorization", "Bearer " + jwt);
        }
}