package com.coworkproject.repository;

import com.coworkproject.model.Desk;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface DeskRepository extends JpaRepository<Desk, Integer> {

    // Método para buscar por número da mesa
    Optional<Desk> findByNumberDesks(Integer numberDesks);

    // Método para verificar se número já existe
    boolean existsByNumberDesks(Integer numberDesks);

    // Método para buscar por nome
    Optional<Desk> findByNameDesks(String nameDesks);

    // Método para verificar se nome já existe
    boolean existsByNameDesks(String nameDesks);
}