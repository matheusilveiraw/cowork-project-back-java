package com.coworkproject.repository;

import com.coworkproject.model.DeskRental;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface DeskRentalRepository extends JpaRepository<DeskRental, Integer> {

    // Buscar aluguéis por cliente
    List<DeskRental> findByCustomerIdCustomers(Integer customerId);

    // Buscar aluguéis por mesa
    List<DeskRental> findByDeskIdDesks(Integer deskId);

    // Buscar aluguéis por plano
    List<DeskRental> findByRentalPlanIdRentalPlans(Integer rentalPlanId);

    // Verificar conflitos de horário para uma mesa
    @Query("SELECT dr FROM DeskRental dr WHERE dr.desk.idDesks = :deskId AND " +
            "((dr.startPeriodDeskRentals BETWEEN :start AND :end) OR " +
            "(dr.endPeriodDeskRentals BETWEEN :start AND :end) OR " +
            "(:start BETWEEN dr.startPeriodDeskRentals AND dr.endPeriodDeskRentals))")
    List<DeskRental> findConflictingRentals(@Param("deskId") Integer deskId,
                                            @Param("start") LocalDateTime start,
                                            @Param("end") LocalDateTime end);

    // Buscar aluguéis ativos (que estão no período de vigência)
    @Query("SELECT dr FROM DeskRental dr WHERE :currentDate BETWEEN dr.startPeriodDeskRentals AND dr.endPeriodDeskRentals")
    List<DeskRental> findActiveRentals(@Param("currentDate") LocalDateTime currentDate);
}