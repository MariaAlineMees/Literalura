package com.alura.literalura.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Entity
@Table(name = "livros")
public class Livro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String titulo;

    private String idiomas;
    private Integer numeroDownloads;

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE})
    @JoinTable(name = "livro_autor",
            joinColumns = @JoinColumn(name = "livro_id"),
            inverseJoinColumns = @JoinColumn(name = "autor_id"))
    private List<Autor> autores = new ArrayList<>();

    public Livro() {}

    public Livro(DadosLivro dadosLivro) {
        this.titulo = Optional.ofNullable(dadosLivro.titulo()).orElse("Título Desconhecido");
        this.idiomas = Optional.ofNullable(dadosLivro.idiomas())
                .map(lista -> String.join(", ", lista))
                .orElse("Idioma Desconhecido");
        this.numeroDownloads = Optional.ofNullable(dadosLivro.numeroDownloads()).orElse(0);
        this.autores = Optional.ofNullable(dadosLivro.autores())
                .map(lista -> lista.stream()
                        .map(d -> new Autor(d.nome().trim(), d.anoNascimento(), d.anoMorte()))
                        .collect(Collectors.toList()))
                .orElse(new ArrayList<>());
    }

    public Long getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getIdiomas() {
        return idiomas;
    }

    public Integer getNumeroDownloads() {
        return numeroDownloads;
    }

    public List<Autor> getAutores() {
        return autores;
    }

    public void setAutores(List<Autor> autores) {
        this.autores = autores;
    }

    @Override
    public String toString() {
        return "Livro{" +
                "título='" + titulo + '\'' +
                ", idiomas='" + idiomas + '\'' +
                ", downloads=" + numeroDownloads +
                ", autores=" + (autores != null ? autores.stream().map(Autor::getNome).collect(Collectors.joining(", ")) : "Nenhum") +
                '}';
    }
}


