package com.coworkproject.repository;

import com.coworkproject.model.RentalShift;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RentalShiftRepository extends JpaRepository<RentalShift, Integer> {

    // Método para buscar por nome
    Optional<RentalShift> findByNameRentalShifts(String nameRentalShifts);

    // Método para verificar se nome já existe
    boolean existsByNameRentalShifts(String nameRentalShifts);
}