package com.coworkproject.dto;

import java.math.BigDecimal;

public class RentalPlanRequest {
    private Integer idRentalCategories;
    private Integer idRentalShifts;
    private String planNameRentalPlans;
    private BigDecimal priceRentalPlans;

    // Getters e Setters
    public Integer getIdRentalCategories() {
        return idRentalCategories;
    }

    public void setIdRentalCategories(Integer idRentalCategories) {
        this.idRentalCategories = idRentalCategories;
    }

    public Integer getIdRentalShifts() {
        return idRentalShifts;
    }

    public void setIdRentalShifts(Integer idRentalShifts) {
        this.idRentalShifts = idRentalShifts;
    }

    public String getPlanNameRentalPlans() {
        return planNameRentalPlans;
    }

    public void setPlanNameRentalPlans(String planNameRentalPlans) {
        this.planNameRentalPlans = planNameRentalPlans;
    }

    public BigDecimal getPriceRentalPlans() {
        return priceRentalPlans;
    }

    public void setPriceRentalPlans(BigDecimal priceRentalPlans) {
        this.priceRentalPlans = priceRentalPlans;
    }
}