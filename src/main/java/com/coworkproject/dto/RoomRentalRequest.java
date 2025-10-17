package com.coworkproject.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class RoomRentalRequest {
    private Integer idRooms;
    private Integer idCustomers;
    private LocalDateTime startPeriodAreaRentals;
    private LocalDateTime endPeriodAreaRentals;
    private BigDecimal totalPriceStandRentals;

    // Construtores
    public RoomRentalRequest() {}

    public RoomRentalRequest(Integer idRooms, Integer idCustomers,
                             LocalDateTime startPeriodAreaRentals, LocalDateTime endPeriodAreaRentals,
                             BigDecimal totalPriceStandRentals) {
        this.idRooms = idRooms;
        this.idCustomers = idCustomers;
        this.startPeriodAreaRentals = startPeriodAreaRentals;
        this.endPeriodAreaRentals = endPeriodAreaRentals;
        this.totalPriceStandRentals = totalPriceStandRentals;
    }

    // Getters e Setters
    public Integer getIdRooms() {
        return idRooms;
    }

    public void setIdRooms(Integer idRooms) {
        this.idRooms = idRooms;
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