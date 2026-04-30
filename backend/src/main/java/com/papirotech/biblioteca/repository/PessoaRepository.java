package com.papirotech.biblioteca.repository;

import com.papirotech.biblioteca.entity.Pessoa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PessoaRepository extends JpaRepository<Pessoa, Integer> {


    Optional<Pessoa> findByCpf(String cpf);

}