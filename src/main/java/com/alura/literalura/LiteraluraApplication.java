package com.alura.literalura;

import com.alura.literalura.principal.Principal;
import com.alura.literalura.repository.AutorRepository;
import com.alura.literalura.repository.LivroRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class LiteraluraApplication {
	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(LiteraluraApplication.class, args);


		AutorRepository autorRepository = context.getBean(AutorRepository.class);
		LivroRepository livroRepository = context.getBean(LivroRepository.class);


		Principal principal = new Principal(livroRepository, autorRepository);
		principal.exibeMenu();
	}
}



