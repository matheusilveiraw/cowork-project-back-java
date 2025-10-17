package com.coworkproject.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class StandRentalRequest {
    private Integer idStands;
    private Integer idCustomers;
    private Integer idRentalPlans;
    private LocalDateTime startPeriodStandRentals;
    private LocalDateTime endPeriodStandRentals;
    private BigDecimal totalPriceStandRentals;

    // Construtores
    public StandRentalRequest() {}

    public StandRentalRequest(Integer idStands, Integer idCustomers, Integer idRentalPlans,
                              LocalDateTime startPeriodStandRentals, LocalDateTime endPeriodStandRentals,
                              BigDecimal totalPriceStandRentals) {
        this.idStands = idStands;
        this.idCustomers = idCustomers;
        this.idRentalPlans = idRentalPlans;
        this.startPeriodStandRentals = startPeriodStandRentals;
        this.endPeriodStandRentals = endPeriodStandRentals;
        this.totalPriceStandRentals = totalPriceStandRentals;
    }

    // Getters e Setters
    public Integer getIdStands() {
        return idStands;
    }

    public void setIdStands(Integer idStands) {
        this.idStands = idStands;
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

    public LocalDateTime getStartPeriodStandRentals() {
        return startPeriodStandRentals;
    }

    public void setStartPeriodStandRentals(LocalDateTime startPeriodStandRentals) {
        this.startPeriodStandRentals = startPeriodStandRentals;
    }

    public LocalDateTime getEndPeriodStandRentals() {
        return endPeriodStandRentals;
    }

    public void setEndPeriodStandRentals(LocalDateTime endPeriodStandRentals) {
        this.endPeriodStandRentals = endPeriodStandRentals;
    }

    public BigDecimal getTotalPriceStandRentals() {
        return totalPriceStandRentals;
    }

    public void setTotalPriceStandRentals(BigDecimal totalPriceStandRentals) {
        this.totalPriceStandRentals = totalPriceStandRentals;
    }
}