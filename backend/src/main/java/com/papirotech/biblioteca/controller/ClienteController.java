package com.papirotech.biblioteca.controller;

import com.papirotech.biblioteca.entity.Cliente;
import com.papirotech.biblioteca.service.impl.UsuarioServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final UsuarioServiceImpl usuarioService;

    // RF06
    @PostMapping
    public ResponseEntity<Cliente> cadastrar(@RequestBody Cliente cliente) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(usuarioService.cadastrarCliente(cliente));
    }

    // RF16
    @PutMapping("/{id}")
    public ResponseEntity<Cliente> editar(@PathVariable Long id,
                                          @RequestBody Cliente cliente) {
        return ResponseEntity.ok(usuarioService.editarCliente(id, cliente));
    }

    // RF16
    @GetMapping("/{id}")
    public ResponseEntity<Cliente> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.buscarPorId(id));
    }

    // RF16
    @GetMapping
    public ResponseEntity<List<Cliente>> listar() {
        return ResponseEntity.ok(usuarioService.listar());
    }
}
