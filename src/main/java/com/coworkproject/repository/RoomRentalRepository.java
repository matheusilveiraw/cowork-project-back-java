package com.coworkproject.repository;

import com.coworkproject.model.RoomRental;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RoomRentalRepository extends JpaRepository<RoomRental, Integer> {

    // Buscar aluguéis por sala
    List<RoomRental> findByRoomIdRooms(Integer roomId);

    // Buscar aluguéis por cliente
    List<RoomRental> findByCustomerIdCustomers(Integer customerId);

    // Buscar aluguéis conflitantes (para validação de datas)
    @Query("SELECT rr FROM RoomRental rr WHERE rr.room.idRooms = :roomId " +
            "AND ((rr.startPeriodAreaRentals <= :endDate AND rr.endPeriodAreaRentals >= :startDate))")
    List<RoomRental> findConflictingRentals(@Param("roomId") Integer roomId,
                                            @Param("startDate") LocalDateTime startDate,
                                            @Param("endDate") LocalDateTime endDate);

    // Buscar aluguéis ativos
    @Query("SELECT rr FROM RoomRental rr WHERE rr.startPeriodAreaRentals <= :currentDate " +
            "AND rr.endPeriodAreaRentals >= :currentDate")
    List<RoomRental> findActiveRentals(@Param("currentDate") LocalDateTime currentDate);
}