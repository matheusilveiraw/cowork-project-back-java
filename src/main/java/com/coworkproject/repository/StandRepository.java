package com.coworkproject.repository;

import com.coworkproject.model.Stand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StandRepository extends JpaRepository<Stand, Integer> {

    // Buscar por número (único)
    Optional<Stand> findByNumberStands(Integer numberStands);

    // Buscar por nome
    Optional<Stand> findByNameStands(String nameStands);

    // Verificar se existe por número
    boolean existsByNumberStands(Integer numberStands);

    // Verificar se existe por nome
    boolean existsByNameStands(String nameStands);

    // Verificar se existe por número excluindo um ID específico (para update)
    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM Stand s WHERE s.numberStands = :number AND s.idStands != :id")
    boolean existsByNumberStandsAndIdStandsNot(@Param("number") Integer number, @Param("id") Integer id);

    // Verificar se existe por nome excluindo um ID específico (para update)
    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM Stand s WHERE s.nameStands = :name AND s.idStands != :id")
    boolean existsByNameStandsAndIdStandsNot(@Param("name") String name, @Param("id") Integer id);
}