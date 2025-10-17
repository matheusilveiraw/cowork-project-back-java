package com.coworkproject.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "areaRentals")
public class AreaRental {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idAreaRentals")
    private Integer idAreaRentals;

    @ManyToOne
    @JoinColumn(name = "idAreas", nullable = false)
    private Area area;

    @ManyToOne
    @JoinColumn(name = "idCustomers", nullable = false)
    private Customer customer;

    @Column(name = "startPeriodAreaRentals", nullable = false)
    private LocalDateTime startPeriodAreaRentals;

    @Column(name = "endPeriodAreaRentals", nullable = false)
    private LocalDateTime endPeriodAreaRentals;

    @Column(name = "totalPriceStandRentals", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPriceStandRentals;

    // Construtores
    public AreaRental() {}

    public AreaRental(Area area, Customer customer,
                      LocalDateTime startPeriodAreaRentals, LocalDateTime endPeriodAreaRentals,
                      BigDecimal totalPriceStandRentals) {
        this.area = area;
        this.customer = customer;
        this.startPeriodAreaRentals = startPeriodAreaRentals;
        this.endPeriodAreaRentals = endPeriodAreaRentals;
        this.totalPriceStandRentals = totalPriceStandRentals;
    }

    // Getters e Setters
    public Integer getIdAreaRentals() {
        return idAreaRentals;
    }

    public void setIdAreaRentals(Integer idAreaRentals) {
        this.idAreaRentals = idAreaRentals;
    }

    public Area getArea() {
        return area;
    }

    public void setArea(Area area) {
        this.area = area;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
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

    // toString
    @Override
    public String toString() {
        return "AreaRental{" +
                "idAreaRentals=" + idAreaRentals +
                ", area=" + area +
                ", customer=" + customer +
                ", startPeriodAreaRentals=" + startPeriodAreaRentals +
                ", endPeriodAreaRentals=" + endPeriodAreaRentals +
                ", totalPriceStandRentals=" + totalPriceStandRentals +
                '}';
    }
}