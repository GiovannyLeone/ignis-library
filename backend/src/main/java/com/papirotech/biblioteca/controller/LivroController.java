package com.papirotech.biblioteca.controller;

import com.papirotech.biblioteca.entity.Livro;
import com.papirotech.biblioteca.service.impl.LivroServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/livros")
@RequiredArgsConstructor
public class LivroController {

    private final LivroServiceImpl livroService;

    // RF04
    @GetMapping("/consulta")
    public ResponseEntity<List<Livro>> consultarAcervo(@RequestParam(required = false) String termo) {
        return ResponseEntity.ok(livroService.consultarAcervo(termo));
    }

    // RF05
    @GetMapping("/disponiveis")
    public ResponseEntity<List<Livro>> listarLivrosParaEmprestimo() {
        return ResponseEntity.ok(livroService.listarLivrosParaEmprestimo());
    }

    // RF11
    @GetMapping("/{id}/disponibilidade")
    public ResponseEntity<Boolean> verificarDisponibilidade(@PathVariable Integer id) {
        return ResponseEntity.ok(livroService.verificarDisponibilidade(id));
    }
}