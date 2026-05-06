package com.papirotech.biblioteca.repository;
import com.papirotech.biblioteca.entity.Acl;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface AclRepository extends JpaRepository<Acl, Integer> {
    Optional<Acl> findByDescricao(String descricao);
}
