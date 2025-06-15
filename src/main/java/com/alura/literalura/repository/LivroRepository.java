package com.alura.literalura.repository;

import com.alura.literalura.model.Livro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LivroRepository extends JpaRepository<Livro, Long> {

    List<Livro> findByTituloContainingIgnoreCase(String titulo);

    @Query("SELECT l FROM Livro l JOIN l.autores a WHERE LOWER(a.nome) LIKE LOWER(CONCAT('%', :nomeAutor, '%'))")
    List<Livro> findByAutoresNomeContainingIgnoreCase(@Param("nomeAutor") String nomeAutor);

    List<Livro> findTop5ByOrderByNumeroDownloadsDesc();

    List<Livro> findByIdiomasContainingIgnoreCase(String idioma);

    @Query("SELECT l FROM Livro l WHERE l.numeroDownloads >= :downloads")
    List<Livro> livrosPorPopularidade(@Param("downloads") int downloads);

    @Query("SELECT l FROM Livro l WHERE LOWER(l.idiomas) LIKE LOWER(CONCAT('%', :idioma, '%')) AND l.numeroDownloads >= :downloads")
    List<Livro> livrosPorIdiomaEPopularidade(@Param("idioma") String idioma, @Param("downloads") int downloads);

    @Query("SELECT l FROM Livro l JOIN FETCH l.autores")
    List<Livro> findAllWithAutores();

    @Query("SELECT l FROM Livro l LEFT JOIN l.autores a WHERE a IS NULL")
    List<Livro> findLivrosSemAutores();
}

