package com.autobots.automanager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import com.autobots.automanager.entidades.Veiculo;
import com.autobots.automanager.enumeracoes.TipoVeiculo;
import com.autobots.automanager.repositorios.VeiculoRepositorio;

// Excluímos a autoconfiguração de segurança padrão para não bloquear o acesso local do Gateway
@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class AutomanagerSistemaVeiculo implements CommandLineRunner {

    @Autowired
    private VeiculoRepositorio veiculoRepositorio;

    public static void main(String[] args) {
        SpringApplication.run(AutomanagerSistemaVeiculo.class, args);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
    @Override
    public void run(String... args) throws Exception {
        
        // Cadastrando Veículo 1
        Veiculo v1 = new Veiculo();
        v1.setEmpresaId(1L);         // Pertence à loja 1
        v1.setModelo("Toyota Etios");
        v1.setPlaca("ABC-1234");
        v1.setTipo(TipoVeiculo.HATCH);
        v1.setProprietarioId(1L);    // ID do cliente (da porta 8081)
        veiculoRepositorio.save(v1);

        // Cadastrando Veículo 2
        Veiculo v2 = new Veiculo();
        v2.setEmpresaId(1L);         // Pertence à loja 1
        v2.setModelo("Honda Civic");
        v2.setPlaca("XYZ-9876");
        v2.setTipo(TipoVeiculo.HATCH);
        v2.setProprietarioId(2L);    // ID do outro cliente
        veiculoRepositorio.save(v2);
        
        System.out.println("Veículos iniciais carregados com sucesso no banco da porta 8087!");
    }
}