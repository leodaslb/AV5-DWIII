package com.autobots.automanager;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

import com.autobots.automanager.entidades.Mercadoria;
import com.autobots.automanager.entidades.Servico;
import com.autobots.automanager.repositorios.MercadoriaRepositorio;
import com.autobots.automanager.repositorios.ServicoRepositorio;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class AutomanagerSistemaMercadorias implements CommandLineRunner {

    @Autowired 
    private MercadoriaRepositorio mercadoriaRepositorio;

    @Autowired
    private ServicoRepositorio servicoRepositorio;

    public static void main(String[] args) {
        SpringApplication.run(AutomanagerSistemaMercadorias.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        
        // --- MERCADORIAS ---
        Mercadoria m1 = new Mercadoria();
        m1.setCadastro(new Date());
        m1.setFabricao(new Date());
        m1.setEmpresaId(1L);
        m1.setNome("Roda de liga leve modelo Toyota Etios");
        m1.setValidade(new Date());
        m1.setQuantidade(30);
        m1.setValor(300.0);
        m1.setDescricao("Roda de liga leve original de fábrica");
        mercadoriaRepositorio.save(m1);

        Mercadoria m2 = new Mercadoria();
        m2.setCadastro(new Date());
        m2.setFabricao(new Date());
        m2.setEmpresaId(1L);
        m2.setNome("Pneu Traseiro Pirelli 130/70-17");
        m2.setValidade(new Date());
        m2.setQuantidade(15);
        m2.setValor(450.0);
        m2.setDescricao("Pneu ideal para Yamaha Fazer 250, excelente aderência na estrada");
        mercadoriaRepositorio.save(m2);

        Mercadoria m3 = new Mercadoria();
        m3.setCadastro(new Date());
        m3.setFabricao(new Date());
        m3.setEmpresaId(2L);
        m3.setNome("Kit Relação com Retentor");
        m3.setValidade(new Date());
        m3.setQuantidade(10);
        m3.setValor(280.0);
        m3.setDescricao("Kit relação completo (coroa, corrente e pinhão) para maior durabilidade");
        mercadoriaRepositorio.save(m3);


        // --- SERVIÇOS ---
        Servico s1 = new Servico();
        s1.setEmpresaId(1L);
        s1.setNome("Troca de Óleo e Filtro");
        s1.setValor(50.0);
        s1.setDescricao("Mão de obra para troca de óleo do motor e filtro");
        servicoRepositorio.save(s1);

        Servico s2 = new Servico();
        s2.setEmpresaId(1L);
        s2.setNome("Revisão Geral de Viagem");
        s2.setValor(250.0);
        s2.setDescricao("Revisão completa de freios, suspensão e motor para pegar a estrada com segurança");
        servicoRepositorio.save(s2);

        Servico s3 = new Servico();
        s3.setEmpresaId(2L);
        s3.setNome("Ajuste e Lubrificação de Corrente");
        s3.setValor(35.0);
        s3.setDescricao("Tensionamento correto e aplicação de lubrificante especial na relação");
        servicoRepositorio.save(s3);

        System.out.println("Mercadorias e Serviços iniciais carregados com sucesso no banco da porta 8083!");
    }
}