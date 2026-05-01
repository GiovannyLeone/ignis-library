package com.papirotech.biblioteca.controller;

import com.papirotech.biblioteca.entity.Livro;
import com.papirotech.biblioteca.service.LivroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/livros")
public class LivroController {

    @Autowired
    private LivroService livroService;

    @PostMapping
    public ResponseEntity<?> adicionarLivro(@RequestBody Livro livro) {
        try {
            Livro livroSalvo = livroService.adicionarLivro(livro);

            // se der certo, retorna o status 201 (Created) e os dados do livro
            return ResponseEntity.status(HttpStatus.CREATED).body(livroSalvo);

        } catch (RuntimeException e) {
            // se o Service lançar aquele erro do ISBN duplicado, vem p ca
            // retorna o status 400 (Bad Request) com a mensagem de erro
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}