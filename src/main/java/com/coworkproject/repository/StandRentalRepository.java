package com.coworkproject.repository;

import com.coworkproject.model.StandRental;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StandRentalRepository extends JpaRepository<StandRental, Integer> {

    // Buscar aluguéis por stand
    List<StandRental> findByStandIdStands(Integer standId);

    // Buscar aluguéis por cliente
    List<StandRental> findByCustomerIdCustomers(Integer customerId);

    // Buscar conflitos de horário
    @Query("SELECT sr FROM StandRental sr WHERE sr.stand.idStands = :standId AND " +
            "(:startDate BETWEEN sr.startPeriodStandRentals AND sr.endPeriodStandRentals OR " +
            ":endDate BETWEEN sr.startPeriodStandRentals AND sr.endPeriodStandRentals OR " +
            "sr.startPeriodStandRentals BETWEEN :startDate AND :endDate)")
    List<StandRental> findTimeRangeConflicts(@Param("standId") Integer standId,
                                             @Param("startDate") LocalDateTime startDate,
                                             @Param("endDate") LocalDateTime endDate);

    // Buscar aluguéis ativos
    @Query("SELECT sr FROM StandRental sr WHERE :currentDate BETWEEN sr.startPeriodStandRentals AND sr.endPeriodStandRentals")
    List<StandRental> findActiveRentals(@Param("currentDate") LocalDateTime currentDate);
}