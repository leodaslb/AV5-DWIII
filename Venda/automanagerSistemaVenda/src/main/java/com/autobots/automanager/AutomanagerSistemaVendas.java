package com.autobots.automanager;

import java.util.Date;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import com.autobots.automanager.entidades.Venda;
import com.autobots.automanager.repositorios.VendaRepositorio;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class AutomanagerSistemaVendas implements CommandLineRunner {

    @Autowired 
    private VendaRepositorio vendaRepositorio;

    public static void main(String[] args) {
        SpringApplication.run(AutomanagerSistemaVendas.class, args);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Override
    public void run(String... args) throws Exception {
        
        Venda venda1 = new Venda();
        venda1.setCadastro(new Date());
        venda1.setIdentificacao("VENDA-2026-001");
        venda1.setEmpresaId(1L);         
        venda1.setClienteId(2L);        
        venda1.setFuncionarioId(3L);     
        venda1.setVeiculoId(1L);         
        venda1.setMercadoriaIds(Set.of(1L, 2L)); 
        venda1.setServicoIds(Set.of(1L));        
        vendaRepositorio.save(venda1);

        Venda venda2 = new Venda();
        venda2.setCadastro(new Date());
        venda2.setIdentificacao("VENDA-2026-002");
        venda2.setEmpresaId(1L);         
        venda2.setClienteId(2L); 
        venda2.setFuncionarioId(2L); 
        venda2.setVeiculoId(1L); 
        venda2.setMercadoriaIds(Set.of(2L)); 
        vendaRepositorio.save(venda2);
        
        Venda venda3 = new Venda();
        venda3.setCadastro(new Date());
        venda3.setIdentificacao("VENDA-2026-003");
        venda3.setEmpresaId(1L);         
        venda3.setClienteId(2L); 
        venda3.setFuncionarioId(3L); 
        venda3.setVeiculoId(1L); 
        venda3.setServicoIds(Set.of(1L, 2L)); 
        vendaRepositorio.save(venda3);
        
        System.out.println("Vendas iniciais carregadas com sucesso no banco da porta 8085!");
    }
}