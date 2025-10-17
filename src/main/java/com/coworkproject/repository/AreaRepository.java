package com.coworkproject.repository;

import com.coworkproject.model.Area;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AreaRepository extends JpaRepository<Area, Integer> {

    // Buscar por número (único)
    Optional<Area> findByNumberAreas(Integer numberAreas);

    // Buscar por nome
    Optional<Area> findByNameAreas(String nameAreas);

    // Verificar se existe por número
    boolean existsByNumberAreas(Integer numberAreas);

    // Verificar se existe por nome
    boolean existsByNameAreas(String nameAreas);

    // Verificar se existe por número excluindo um ID específico (para update)
    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM Area a WHERE a.numberAreas = :number AND a.idAreas != :id")
    boolean existsByNumberAreasAndIdAreasNot(@Param("number") Integer number, @Param("id") Integer id);

    // Verificar se existe por nome excluindo um ID específico (para update)
    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM Area a WHERE a.nameAreas = :name AND a.idAreas != :id")
    boolean existsByNameAreasAndIdAreasNot(@Param("name") String name, @Param("id") Integer id);
}