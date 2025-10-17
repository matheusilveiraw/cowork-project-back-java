package com.coworkproject.repository;

import com.coworkproject.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Integer> {

    // Buscar por número (único)
    Optional<Room> findByNumberRooms(Integer numberRooms);

    // Buscar por nome
    Optional<Room> findByNameRooms(String nameRooms);

    // Verificar se existe por número
    boolean existsByNumberRooms(Integer numberRooms);

    // Verificar se existe por nome
    boolean existsByNameRooms(String nameRooms);

    // Verificar se existe por número excluindo um ID específico (para update)
    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM Room r WHERE r.numberRooms = :number AND r.idRooms != :id")
    boolean existsByNumberRoomsAndIdRoomsNot(@Param("number") Integer number, @Param("id") Integer id);

    // Verificar se existe por nome excluindo um ID específico (para update)
    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM Room r WHERE r.nameRooms = :name AND r.idRooms != :id")
    boolean existsByNameRoomsAndIdRoomsNot(@Param("name") String name, @Param("id") Integer id);
}