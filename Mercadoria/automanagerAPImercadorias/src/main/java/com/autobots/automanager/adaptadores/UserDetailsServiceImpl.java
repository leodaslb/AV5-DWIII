package com.autobots.automanager.adaptadores;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.autobots.automanager.dto.UsuarioResDto;


@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private RestTemplate restTemplate;

    private static final String SISTEMA_URL = "http://localhost:8081/usuario";

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            ResponseEntity<UsuarioResDto[]> response = restTemplate.exchange(
                SISTEMA_URL + "/usuarios",
                HttpMethod.GET,
                null,
                UsuarioResDto[].class
            );
             System.out.println("Resposta recebida do sistema");
            UsuarioResDto[] usuarios = response.getBody();
            if (usuarios == null) throw new UsernameNotFoundException(username);

            for (UsuarioResDto usuario : usuarios) {
                if (usuario.getCredencial().getNomeUsuario().equals(username)) {
                    return new UserDetailsImpl(usuario);
                }
            }
        } catch (Exception e) {
            System.out.println("ERRO ao chamar sistema: " + e.getMessage());
        throw new UsernameNotFoundException(username);

        }
        throw new UsernameNotFoundException(username);
    }
}