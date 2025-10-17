package com.coworkproject.repository;

import com.coworkproject.model.AreaRental;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AreaRentalRepository extends JpaRepository<AreaRental, Integer> {

    // Buscar aluguéis por área
    List<AreaRental> findByAreaIdAreas(Integer areaId);

    // Buscar aluguéis por cliente
    List<AreaRental> findByCustomerIdCustomers(Integer customerId);

    // Buscar aluguéis conflitantes (para validação de datas)
    @Query("SELECT ar FROM AreaRental ar WHERE ar.area.idAreas = :areaId " +
            "AND ((ar.startPeriodAreaRentals <= :endDate AND ar.endPeriodAreaRentals >= :startDate))")
    List<AreaRental> findConflictingRentals(@Param("areaId") Integer areaId,
                                            @Param("startDate") LocalDateTime startDate,
                                            @Param("endDate") LocalDateTime endDate);

    // Buscar aluguéis ativos
    @Query("SELECT ar FROM AreaRental ar WHERE ar.startPeriodAreaRentals <= :currentDate " +
            "AND ar.endPeriodAreaRentals >= :currentDate")
    List<AreaRental> findActiveRentals(@Param("currentDate") LocalDateTime currentDate);
}