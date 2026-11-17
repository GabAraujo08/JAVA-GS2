package br.com.fiap.presente.idoso.repository;


import br.com.fiap.presente.idoso.entity.Idoso;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IdosoRepository extends JpaRepository<Idoso, Long> {
}
