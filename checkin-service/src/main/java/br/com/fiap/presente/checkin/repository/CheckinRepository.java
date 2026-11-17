package br.com.fiap.presente.checkin.repository;


import br.com.fiap.presente.checkin.entity.Checkin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CheckinRepository extends JpaRepository<Checkin, Long> {
}
