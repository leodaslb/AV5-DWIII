package com.autobots.automanager.filtros;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import com.autobots.automanager.jwt.ProvedorJwt;
import java.util.List;

class AutenticadorJwt {
    private String jwt;
    private ProvedorJwt provedorJwt;
    
    public AutenticadorJwt(String jwt, ProvedorJwt provedorJwt) {
        this.jwt = jwt;
        this.provedorJwt = provedorJwt;
    }

    public UsernamePasswordAuthenticationToken obterAutenticacao() {
        if (provedorJwt.validarJwt(jwt)) {
            String nomeUsuario = provedorJwt.obterNomeUsuario(jwt);
            List<GrantedAuthority> autoridades = provedorJwt.obterPerfis(jwt);
            
            // Cria o token de autenticação sem precisar de senha ou de buscar no banco!
            return new UsernamePasswordAuthenticationToken(nomeUsuario, null, autoridades);
        }
        return null;
    }
}