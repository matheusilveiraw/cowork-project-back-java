package com.coworkproject.repository;

import com.coworkproject.model.RentalCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RentalCategoryRepository extends JpaRepository<RentalCategory, Integer> {

    // Método para buscar por nome
    Optional<RentalCategory> findByNameRentalCategories(String nameRentalCategories);

    // Método para verificar se nome já existe
    boolean existsByNameRentalCategories(String nameRentalCategories);
}