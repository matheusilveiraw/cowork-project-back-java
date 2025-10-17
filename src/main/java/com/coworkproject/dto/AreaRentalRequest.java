package com.coworkproject.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AreaRentalRequest {
    private Integer idAreas;
    private Integer idCustomers;
    private LocalDateTime startPeriodAreaRentals;
    private LocalDateTime endPeriodAreaRentals;
    private BigDecimal totalPriceStandRentals;

    // Construtores
    public AreaRentalRequest() {}

    public AreaRentalRequest(Integer idAreas, Integer idCustomers,
                             LocalDateTime startPeriodAreaRentals, LocalDateTime endPeriodAreaRentals,
                             BigDecimal totalPriceStandRentals) {
        this.idAreas = idAreas;
        this.idCustomers = idCustomers;
        this.startPeriodAreaRentals = startPeriodAreaRentals;
        this.endPeriodAreaRentals = endPeriodAreaRentals;
        this.totalPriceStandRentals = totalPriceStandRentals;
    }

    // Getters e Setters
    public Integer getIdAreas() {
        return idAreas;
    }

    public void setIdAreas(Integer idAreas) {
        this.idAreas = idAreas;
    }

    public Integer getIdCustomers() {
        return idCustomers;
    }

    public void setIdCustomers(Integer idCustomers) {
        this.idCustomers = idCustomers;
    }

    public LocalDateTime getStartPeriodAreaRentals() {
        return startPeriodAreaRentals;
    }

    public void setStartPeriodAreaRentals(LocalDateTime startPeriodAreaRentals) {
        this.startPeriodAreaRentals = startPeriodAreaRentals;
    }

    public LocalDateTime getEndPeriodAreaRentals() {
        return endPeriodAreaRentals;
    }

    public void setEndPeriodAreaRentals(LocalDateTime endPeriodAreaRentals) {
        this.endPeriodAreaRentals = endPeriodAreaRentals;
    }

    public BigDecimal getTotalPriceStandRentals() {
        return totalPriceStandRentals;
    }

    public void setTotalPriceStandRentals(BigDecimal totalPriceStandRentals) {
        this.totalPriceStandRentals = totalPriceStandRentals;
    }
}