package com.autobots.automanager;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.client.RestTemplate;

import com.autobots.automanager.entidades.Credencial;
import com.autobots.automanager.entidades.Empresa;
import com.autobots.automanager.entidades.Usuario;
import com.autobots.automanager.modelos.Perfil;
import com.autobots.automanager.repositorios.EmpresaRepositorio;
import com.autobots.automanager.repositorios.UsuarioRepositorio;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class AutomanagerSistemaUsuario implements CommandLineRunner {

    @Autowired 
    private UsuarioRepositorio repositorio;

    @Autowired
    private EmpresaRepositorio empresaRepositorio;

    public static void main(String[] args) {
        SpringApplication.run(AutomanagerSistemaUsuario.class, args);
    }
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Override
    public void run(String... args) throws Exception {
        BCryptPasswordEncoder codificador = new BCryptPasswordEncoder();
        
        Empresa empresa1 = new Empresa();
        empresa1.setRazaoSocial("Autobots Oficina Mecânica LTDA");
        empresa1.setNomeFantasia("Autobots Centro Automotivo");
        empresa1.setCadastro(new Date());
        empresaRepositorio.save(empresa1);

        Empresa empresa2 = new Empresa();
        empresa2.setRazaoSocial("Decepticons Peças e Serviços S.A.");
        empresa2.setNomeFantasia("Decepticons Moto Peças");
        empresa2.setCadastro(new Date());
        empresaRepositorio.save(empresa2);

        Empresa empresa3 = new Empresa();
        empresa3.setRazaoSocial("Cybertron Concessionária LTDA");
        empresa3.setNomeFantasia("Cybertron Veículos");
        empresa3.setCadastro(new Date());
        empresaRepositorio.save(empresa3);

        Usuario usuario = new Usuario();
        usuario.setNome("administrador");
        usuario.setEmpresaId(1L); 
        usuario.getPerfis().add(Perfil.ROLE_ADMIN);
        
        Credencial credencial = new Credencial();
        credencial.setNomeUsuario("admin");
        credencial.setSenha(codificador.encode("123456"));
        usuario.setCredencial(credencial);
        repositorio.save(usuario);

        Usuario usuario3 = new Usuario();
        usuario3.setNome("cliente teste");
        usuario3.setEmpresaId(1L); 
        usuario3.getPerfis().add(Perfil.ROLE_CLIENTE);
        
        Credencial credencial3 = new Credencial();
        credencial3.setNomeUsuario("cliente3");
        credencial3.setSenha(codificador.encode("123456"));
        usuario3.setCredencial(credencial3);
        repositorio.save(usuario3);

        Usuario usuario4 = new Usuario();
        usuario4.setNome("vendedor teste");
        usuario4.setEmpresaId(2L); 
        usuario4.getPerfis().add(Perfil.ROLE_VENDEDOR);
        
        Credencial credencial4 = new Credencial();
        credencial4.setNomeUsuario("vendedor");
        credencial4.setSenha(codificador.encode("123456"));
        usuario4.setCredencial(credencial4);
        repositorio.save(usuario4);
    }
}