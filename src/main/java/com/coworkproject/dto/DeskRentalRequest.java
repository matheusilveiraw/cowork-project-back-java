package com.coworkproject.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class DeskRentalRequest {
    private Integer idDesks;
    private Integer idCustomers;
    private Integer idRentalPlans;
    private LocalDateTime startPeriodDeskRentals;
    private BigDecimal totalPriceDeskRentals;

    // Getters e Setters
    public Integer getIdDesks() {
        return idDesks;
    }

    public void setIdDesks(Integer idDesks) {
        this.idDesks = idDesks;
    }

    public Integer getIdCustomers() {
        return idCustomers;
    }

    public void setIdCustomers(Integer idCustomers) {
        this.idCustomers = idCustomers;
    }

    public Integer getIdRentalPlans() {
        return idRentalPlans;
    }

    public void setIdRentalPlans(Integer idRentalPlans) {
        this.idRentalPlans = idRentalPlans;
    }

    public LocalDateTime getStartPeriodDeskRentals() {
        return startPeriodDeskRentals;
    }

    public void setStartPeriodDeskRentals(LocalDateTime startPeriodDeskRentals) {
        this.startPeriodDeskRentals = startPeriodDeskRentals;
    }

    public BigDecimal getTotalPriceDeskRentals() {
        return totalPriceDeskRentals;
    }

    public void setTotalPriceDeskRentals(BigDecimal totalPriceDeskRentals) {
        this.totalPriceDeskRentals = totalPriceDeskRentals;
    }
}