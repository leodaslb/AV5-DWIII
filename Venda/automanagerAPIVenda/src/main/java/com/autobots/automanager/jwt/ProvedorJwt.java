package com.autobots.automanager.jwt;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProvedorJwt {
    @Value("${jwt.secret}")
    private String assinatura; 

    public Claims obterReivindicacoes(String jwt) {
        try {
            return Jwts.parser().setSigningKey(assinatura.getBytes()).parseClaimsJws(jwt).getBody();
        } catch (Exception e) {
            return null;
        }
    }

    public boolean validarJwt(String jwt) {
        Claims claims = obterReivindicacoes(jwt);
        if (claims != null) {
            return claims.getExpiration().after(new java.util.Date());
        }
        return false;
    }

    public String obterNomeUsuario(String jwt) {
        return obterReivindicacoes(jwt).getSubject();
    }

    
    public List<GrantedAuthority> obterPerfis(String jwt) {
        Claims claims = obterReivindicacoes(jwt);
        List<String> perfis = claims.get("perfis", List.class);
        return perfis.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }
}