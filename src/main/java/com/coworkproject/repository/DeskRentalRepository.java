package com.coworkproject.repository;

import com.coworkproject.model.DeskRental;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface DeskRentalRepository extends JpaRepository<DeskRental, Integer> {

    // Buscar aluguéis por cliente
    List<DeskRental> findByCustomerIdCustomers(Integer customerId);

    // Buscar aluguéis por mesa
    List<DeskRental> findByDeskIdDesks(Integer deskId);

    // Buscar aluguéis por plano
    List<DeskRental> findByRentalPlanIdRentalPlans(Integer rentalPlanId);

    // Método ORIGINAL para verificar conflitos (mantido para compatibilidade)
    @Query("SELECT dr FROM DeskRental dr WHERE dr.desk.idDesks = :deskId AND " +
            "((dr.startPeriodDeskRentals < :endDate AND dr.endPeriodDeskRentals > :startDate))")
    List<DeskRental> findConflictingRentals(
            @Param("deskId") Integer deskId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    // NOVO MÉTODO - Verificação de conflitos mais específica considerando horários
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

    // NOVO MÉTODO - Verificação de conflitos por turno específico
    @Query("SELECT dr FROM DeskRental dr WHERE dr.desk.idDesks = :deskId AND " +
            "((:startDate BETWEEN dr.startPeriodDeskRentals AND dr.endPeriodDeskRentals) OR " +
            "(:endDate BETWEEN dr.startPeriodDeskRentals AND dr.endPeriodDeskRentals) OR " +
            "(dr.startPeriodDeskRentals BETWEEN :startDate AND :endDate) OR " +
            "(dr.endPeriodDeskRentals BETWEEN :startDate AND :endDate)) AND " +
            "dr.rentalPlan.rentalShift.idRentalShifts = :shiftId")
    List<DeskRental> findTimeRangeConflictsByShift(
            @Param("deskId") Integer deskId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("shiftId") Integer shiftId
    );

    // Buscar aluguéis ativos (que estão no período de vigência)
    @Query("SELECT dr FROM DeskRental dr WHERE :currentDate BETWEEN dr.startPeriodDeskRentals AND dr.endPeriodDeskRentals")
    List<DeskRental> findActiveRentals(@Param("currentDate") LocalDateTime currentDate);
}