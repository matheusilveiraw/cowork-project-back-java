package com.coworkproject.repository;

import com.coworkproject.model.StandRental;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StandRentalRepository extends JpaRepository<StandRental, Integer> {

    @Query("SELECT sr FROM StandRental sr WHERE sr.stand.idStands = :standId")
    List<StandRental> findByStandIdStands(@Param("standId") Integer standId);

    List<StandRental> findByCustomerIdCustomers(Integer customerId);
}