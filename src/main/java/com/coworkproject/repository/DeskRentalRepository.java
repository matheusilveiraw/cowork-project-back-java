package com.coworkproject.repository;

import com.coworkproject.model.DeskRental;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public interface DeskRentalRepository extends JpaRepository<DeskRental, Integer> {

    // Buscar aluguéis por cliente
    List<DeskRental> findByCustomerIdCustomers(Integer customerId);

    // Buscar aluguéis por mesa
    List<DeskRental> findByDeskIdDesks(Integer deskId);

    // Buscar aluguéis por plano
    List<DeskRental> findByRentalPlanIdRentalPlans(Integer rentalPlanId);

    // NOVO MÉTODO - Verificação de conflitos baseada em sobreposição de horários
    @Query("SELECT dr FROM DeskRental dr WHERE dr.desk.idDesks = :deskId AND " +
            "((:startDate BETWEEN dr.startPeriodDeskRentals AND dr.endPeriodDeskRentals) OR " +
            "(:endDate BETWEEN dr.startPeriodDeskRentals AND dr.endPeriodDeskRentals) OR " +
            "(dr.startPeriodDeskRentals BETWEEN :startDate AND :endDate) OR " +
            "(dr.endPeriodDeskRentals BETWEEN :startDate AND :endDate))")
    List<DeskRental> findTimeRangeConflicts(
            @Param("deskId") Integer deskId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    // Buscar aluguéis ativos (que estão no período de vigência)
    @Query("SELECT dr FROM DeskRental dr WHERE :currentDate BETWEEN dr.startPeriodDeskRentals AND dr.endPeriodDeskRentals")
    List<DeskRental> findActiveRentals(@Param("currentDate") LocalDateTime currentDate);
}