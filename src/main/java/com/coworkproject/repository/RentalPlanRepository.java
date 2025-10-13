package com.coworkproject.repository;

import com.coworkproject.model.RentalPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface RentalPlanRepository extends JpaRepository<RentalPlan, Integer> {

    // Buscar planos por categoria
    List<RentalPlan> findByRentalCategoryIdRentalCategories(Integer categoryId);

    // Buscar planos por turno
    List<RentalPlan> findByRentalShiftIdRentalShifts(Integer shiftId);

    // Buscar plano por nome
    Optional<RentalPlan> findByPlanNameRentalPlans(String planNameRentalPlans);

    // Verificar se nome já existe
    boolean existsByPlanNameRentalPlans(String planNameRentalPlans);

    // Buscar planos por categoria e turno
    Optional<RentalPlan> findByRentalCategoryIdRentalCategoriesAndRentalShiftIdRentalShifts(
            Integer categoryId, Integer shiftId);

    // Buscar planos com preço menor ou igual a um valor
    List<RentalPlan> findByPriceRentalPlansLessThanEqual(BigDecimal maxPrice);

    // Buscar planos com preço entre dois valores
    List<RentalPlan> findByPriceRentalPlansBetween(BigDecimal minPrice, BigDecimal maxPrice);

    // Buscar planos por categoria, turno e preço máximo
    @Query("SELECT rp FROM RentalPlan rp WHERE rp.rentalCategory.idRentalCategories = :categoryId " +
            "AND rp.rentalShift.idRentalShifts = :shiftId AND rp.priceRentalPlans <= :maxPrice")
    List<RentalPlan> findPlansByCategoryShiftAndMaxPrice(@Param("categoryId") Integer categoryId,
                                                         @Param("shiftId") Integer shiftId,
                                                         @Param("maxPrice") BigDecimal maxPrice);
}