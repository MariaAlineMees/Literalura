package com.alura.literalura.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Entity
@Table(name = "autores")
public class Autor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String nome;

    private Integer anoNascimento;
    private Integer anoMorte;

    @ManyToMany(mappedBy = "autores")
    private List<Livro> livros = new ArrayList<>();

    public Autor() {}

    public Autor(String nome, Integer anoNascimento, Integer anoMorte) {
        this.nome = nome;
        this.anoNascimento = anoNascimento;
        this.anoMorte = anoMorte;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public Integer getAnoNascimento() {
        return anoNascimento;
    }

    public Integer getAnoMorte() {
        return anoMorte;
    }

    public List<Livro> getLivros() {
        return livros;
    }

    public void setLivros(List<Livro> livros) {
        this.livros = livros;
    }

    @Override
    public String toString() {
        return "Autor{" +
                "nome='" + nome + '\'' +
                ", nascimento=" + anoNascimento +
                ", morte=" + anoMorte +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Autor)) return false;
        Autor autor = (Autor) o;
        return Objects.equals(nome, autor.nome);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nome);
    }
}