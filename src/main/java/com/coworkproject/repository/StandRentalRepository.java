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

    // Buscar aluguéis conflitantes (para validação de datas)
    @Query("SELECT sr FROM StandRental sr WHERE sr.stand.idStands = :standId " +
            "AND ((sr.startPeriodStandRentals <= :endDate AND sr.endPeriodStandRentals >= :startDate))")
    List<StandRental> findConflictingRentals(@Param("standId") Integer standId,
                                             @Param("startDate") LocalDateTime startDate,
                                             @Param("endDate") LocalDateTime endDate);

    // Buscar aluguéis ativos
    @Query("SELECT sr FROM StandRental sr WHERE sr.startPeriodStandRentals <= :currentDate " +
            "AND sr.endPeriodStandRentals >= :currentDate")
    List<StandRental> findActiveRentals(@Param("currentDate") LocalDateTime currentDate);
}