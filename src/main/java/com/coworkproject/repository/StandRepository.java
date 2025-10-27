package com.coworkproject.repository;

import com.coworkproject.model.Stand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StandRepository extends JpaRepository<Stand, Integer> {

    // Verificar se existe stand com o mesmo número
    boolean existsByNumberStands(Integer numberStands);

    // Verificar se existe stand com o mesmo nome
    boolean existsByNameStands(String nameStands);

    // Buscar stand por número
    Optional<Stand> findByNumberStands(Integer numberStands);

    // Buscar stand por nome
    Optional<Stand> findByNameStands(String nameStands);

    // Verificar se existe outro stand com o mesmo número (excluindo um ID específico)
    boolean existsByNumberStandsAndIdStandsNot(Integer numberStands, Integer idStands);

    // Verificar se existe outro stand com o mesmo nome (excluindo um ID específico)
    boolean existsByNameStandsAndIdStandsNot(String nameStands, Integer idStands);
}