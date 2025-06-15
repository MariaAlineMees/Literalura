package com.alura.literalura.principal;

import com.alura.literalura.model.Autor;
import com.alura.literalura.model.DadosLivro;
import com.alura.literalura.model.Livro;
import com.alura.literalura.repository.AutorRepository;
import com.alura.literalura.repository.LivroRepository;
import com.alura.literalura.service.ConsumoAPI;
import com.alura.literalura.service.ConverteDados;
import com.alura.literalura.service.DadosGutendex;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.ReactiveWrappers;

import java.util.*;
import java.util.stream.Collectors;

public class Principal {

    private final Scanner leitura = new Scanner(System.in);
    private final ConsumoAPI consumo = new ConsumoAPI();
    private final ConverteDados conversor = new ConverteDados();
    private final String ENDERECO = "https://gutendex.com/books/?search=";
    private final LivroRepository repositorio;
    private final AutorRepository autorRepository;


    public Principal(LivroRepository repositorio, AutorRepository autorRepository) {
        this.repositorio = repositorio;
        this.autorRepository = autorRepository;
    }

    public void exibeMenu() {
        int opcao = -1;
        while (opcao != 0) {
            String menu = """
                    Escolha o número da sua opção:
                    1 - Buscar livro pelo título
                    2 - Listar livros registrados
                    3 - Listar autores registrados
                    4 - Listar autores vivos em um determinado ano
                    5 - Listar livros em um determinado idioma
                     
                    0 - Sair
                    """;
            System.out.println(menu);

            try {
                opcao = Integer.parseInt(leitura.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Opção inválida. Tente novamente.");
                continue;
            }

            switch (opcao) {
                case 1 -> buscarLivro();
                case 2 -> listarLivrosRegistrados();
                case 3 -> listarAutoresRegistrados();
                case 4 -> listarAutoresVivosPorData();
                case 5 -> listarLivrosPeloIdioma();
                case 0 -> System.out.println("Saindo...");
                default -> System.out.println("Opção inválida.");
            }
        }
    }

    private void buscarLivro() {
        System.out.println("Digite o título do livro para busca:");
        String tituloLivro = leitura.nextLine().trim();

        if (tituloLivro.isEmpty()) {
            System.out.println("Título inválido, tente novamente.");
            return;
        }

        List<Livro> livrosEncontrados = repositorio.findByTituloContainingIgnoreCase(tituloLivro);

        if (!livrosEncontrados.isEmpty()) {
            System.out.println("Livro(s) encontrado(s):");
            livrosEncontrados.forEach(System.out::println);
        } else {
            System.out.println("Livro não encontrado no banco, buscando na API...");
            buscarLivroWeb(tituloLivro);
        }
    }

    private void buscarLivroWeb(String tituloLivro) {
        String json = consumo.obterDados(ENDERECO + tituloLivro.replace(" ", "+"));
        DadosGutendex dadosGutendex = conversor.obterDados(json, DadosGutendex.class);

        if (dadosGutendex.getResults() == null || dadosGutendex.getResults().isEmpty()) {
            System.out.println("Nenhum livro encontrado na API.");
            return;
        }

        System.out.println("Livros encontrados na API:");
        for (int i = 0; i < dadosGutendex.getResults().size(); i++) {
            DadosLivro dLivro = dadosGutendex.getResults().get(i);
            System.out.println(i + ": " + dLivro.titulo());
        }

        System.out.println("Digite o número do livro que você quer salvar:");
        int escolha = Integer.parseInt(leitura.nextLine());

        if (escolha < 0 || escolha >= dadosGutendex.getResults().size()) {
            System.out.println("Opção inválida.");
            return;
        }

        DadosLivro dadosLivro = dadosGutendex.getResults().get(escolha);
        Livro livro = new Livro(dadosLivro);

        // 🔥 Buscando autores no banco ou criando novos antes de salvar o livro 🔥
        List<Autor> autores = dadosLivro.autores().stream()
                .map(d -> autorRepository.findByNome(d.nome()).orElseGet(() -> {
                    Autor novoAutor = new Autor(d.nome(), d.anoNascimento(), d.anoMorte());
                    return autorRepository.save(novoAutor);
                }))
                .collect(Collectors.toList());

        if (autores.isEmpty()) {
            System.out.println("Erro: Nenhum autor encontrado para este livro. Não será salvo.");
            return;
        }

        // 🔥 Vinculando os autores ao livro antes de salvar 🔥
        livro.setAutores(autores);

        // 🚀 Agora salvamos o livro com autores corretamente vinculados 🚀
        repositorio.save(livro);
        System.out.println("Livro salvo com sucesso:\n" + livro);
    }


    private void listarLivrosRegistrados() {
        List<Livro> livros = repositorio.findAllWithAutores();

        if (livros.isEmpty()) {
            System.out.println("Nenhum livro registrado.");
        } else {
            livros.forEach(livro -> Hibernate.initialize(livro.getAutores())); // 🔥 Isso evita autores null!
            livros.forEach(System.out::println);
        }
    }


    private void listarAutoresRegistrados() {
        List<Livro> livros = repositorio.findAllWithAutores(); // Aqui usamos a nova query

        Set<Autor> autoresUnique = livros.stream()
                .flatMap(livro -> livro.getAutores().stream())
                .collect(Collectors.toSet());

        if (autoresUnique.isEmpty()) {
            System.out.println("Nenhum autor registrado.");
        } else {
            System.out.println("Autores registrados:");
            autoresUnique.forEach(System.out::println);
        }
    }


    private void listarAutoresVivosPorData() {
        System.out.println("Digite um ano para buscar autores vivos nesse período:");
        int ano;
        try {
            ano = Integer.parseInt(leitura.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Ano inválido, tente novamente.");
            return;
        }
        List<Livro> livros = repositorio.findAll();
        List<Livro> livrosComAutorVivo = livros.stream()
                .filter(l -> verificarSeAutorEstavaVivo(l.getAutores(), ano))
                .collect(Collectors.toList());

        if (livrosComAutorVivo.isEmpty()) {
            System.out.println("Nenhum autor encontrado nesse período.");
        } else {
            livrosComAutorVivo.forEach(l ->
                    System.out.println(l.getAutores() + " - Livro: " + l.getTitulo()));
        }
    }

    private boolean verificarSeAutorEstavaVivo(List<Autor> autores, int ano) {
        if (autores == null || autores.isEmpty()) {
            return false;
        }
        for (Autor autor : autores) {
            Integer anoNascimento = autor.getAnoNascimento();
            Integer anoMorte = autor.getAnoMorte();
            // Verifica se o autor nasceu até o ano informado e se não faleceu antes desse ano.
            if (anoNascimento != null && anoNascimento <= ano && (anoMorte == null || ano <= anoMorte)) {
                return true;
            }
        }
        return false;
    }

    private void listarLivrosPeloIdioma() {
        System.out.println("Digite o idioma para buscar livros (exemplo: 'en', 'pt'):");
        String idioma = leitura.nextLine().trim();
        if (idioma.isEmpty()) {
            System.out.println("Idioma inválido, tente novamente.");
            return;
        }
        List<Livro> livrosPorIdioma = repositorio.findByIdiomasContainingIgnoreCase(idioma);
        if (livrosPorIdioma.isEmpty()) {
            System.out.println("Nenhum livro encontrado nesse idioma.");
        } else {
            livrosPorIdioma.forEach(System.out::println);
        }
    }
}

