package com.papirotech.biblioteca.controller;

import com.papirotech.biblioteca.service.FavoritoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/favoritos")
public class FavoritoController {

    @Autowired
    private FavoritoService favoritoService;

    @PostMapping("/{pessoaId}/{livroId}")
    public ResponseEntity<String> favoritarLivro(@PathVariable Integer pessoaId, @PathVariable Integer livroId) {

        favoritoService.favoritarLivro(pessoaId, livroId);

        return ResponseEntity.ok("Ação de favorito atualizada com sucesso!!");
    }
}
