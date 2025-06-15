package com.alura.literalura.service;

import com.alura.literalura.model.DadosLivro;
import com.alura.literalura.model.Livro;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;


@JsonIgnoreProperties(ignoreUnknown = true)
public class DadosGutendex {
    @JsonAlias("results")
    private List<DadosLivro> results;

    public List<DadosLivro> getResults() {
        return results;
    }
}
