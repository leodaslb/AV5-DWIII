package com.autobots.automanager.jwt;

import java.util.Date;
import java.util.List;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

class GeradorJwt {
	private String assinatura;
	private Date expiracao;

	public GeradorJwt(String assinatura, long duracao) {
		this.assinatura = assinatura;
		this.expiracao = new Date(System.currentTimeMillis() + duracao);
	}

	public String gerarJwt(String nomeUsuario, List<String> perfis) {
    String jwt = Jwts.builder()
            .setSubject(nomeUsuario)
            .claim("perfis", perfis) 
            .setExpiration(this.expiracao)
            .signWith(SignatureAlgorithm.HS512, this.assinatura.getBytes()).compact();
    return jwt;
}
}