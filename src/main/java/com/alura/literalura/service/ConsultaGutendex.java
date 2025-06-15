package com.alura.literalura.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class ConsultaGutendex {
    public static class ConsultaMyMemory {
        public static String obterTraducao(String text) {
            ObjectMapper mapper = new ObjectMapper();
            ConsumoAPI consumo = new ConsumoAPI();
            String texto = URLEncoder.encode(text, StandardCharsets.UTF_8);
            String langpair = URLEncoder.encode("en|pt-br", StandardCharsets.UTF_8);
            String url = "https://api.mymemory.translated.net/get?q=" + texto + "&langpair=" + langpair;
            String json = consumo.obterDados(url);
            DadosTraducao traducao;
            try {
                traducao = mapper.readValue(json, DadosTraducao.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Erro ao obter tradução", e);
            }
            return traducao.dadosResposta().textoTraduzido();
        }
    }
}
