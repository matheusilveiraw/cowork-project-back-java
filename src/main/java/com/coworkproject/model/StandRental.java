package com.coworkproject.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "standRentals")
public class StandRental {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idStandRentals")
    private Integer idStandRentals;

    @ManyToOne
    @JoinColumn(name = "idStands", nullable = false)
    private Stand stand;

    @ManyToOne
    @JoinColumn(name = "idCustomers", nullable = false)
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "idRentalPlans", nullable = false)
    private RentalPlan rentalPlan;

    @Column(name = "startPeriodStandRentals", nullable = false)
    private LocalDateTime startPeriodStandRentals;

    @Column(name = "endPeriodStandRentals", nullable = false)
    private LocalDateTime endPeriodStandRentals;

    @Column(name = "totalPriceStandRentals", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPriceStandRentals;

    // Construtores
    public StandRental() {}

    public StandRental(Stand stand, Customer customer, RentalPlan rentalPlan,
                       LocalDateTime startPeriodStandRentals, LocalDateTime endPeriodStandRentals,
                       BigDecimal totalPriceStandRentals) {
        this.stand = stand;
        this.customer = customer;
        this.rentalPlan = rentalPlan;
        this.startPeriodStandRentals = startPeriodStandRentals;
        this.endPeriodStandRentals = endPeriodStandRentals;
        this.totalPriceStandRentals = totalPriceStandRentals;
    }

    // Getters e Setters
    public Integer getIdStandRentals() {
        return idStandRentals;
    }

    public void setIdStandRentals(Integer idStandRentals) {
        this.idStandRentals = idStandRentals;
    }

    public Stand getStand() {
        return stand;
    }

    public void setStand(Stand stand) {
        this.stand = stand;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public RentalPlan getRentalPlan() {
        return rentalPlan;
    }

    public void setRentalPlan(RentalPlan rentalPlan) {
        this.rentalPlan = rentalPlan;
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

    // toString
    @Override
    public String toString() {
        return "StandRental{" +
                "idStandRentals=" + idStandRentals +
                ", stand=" + stand +
                ", customer=" + customer +
                ", rentalPlan=" + rentalPlan +
                ", startPeriodStandRentals=" + startPeriodStandRentals +
                ", endPeriodStandRentals=" + endPeriodStandRentals +
                ", totalPriceStandRentals=" + totalPriceStandRentals +
                '}';
    }
}