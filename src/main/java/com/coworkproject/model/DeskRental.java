package com.coworkproject.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "deskRentals")
public class DeskRental {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`idDeskRentals`")
    private Integer idDeskRentals;

    @ManyToOne
    @JoinColumn(name = "`idDesks`", referencedColumnName = "`idDesks`")
    private Desk desk;

    @ManyToOne
    @JoinColumn(name = "`idCustomers`", referencedColumnName = "`idCustomers`")
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "`idRentalPlans`", referencedColumnName = "`idRentalPlans`")
    private RentalPlan rentalPlan;

    @Column(name = "`startPeriodDeskRentals`")
    private LocalDateTime startPeriodDeskRentals;

    @Column(name = "`endPeriodDeskRentals`")
    private LocalDateTime endPeriodDeskRentals;

    @Column(name = "`totalPriceDeskRentals`")
    private BigDecimal totalPriceDeskRentals;

    // Construtores
    public DeskRental() {}

    public DeskRental(Desk desk, Customer customer, RentalPlan rentalPlan,
                      LocalDateTime startPeriodDeskRentals, LocalDateTime endPeriodDeskRentals,
                      BigDecimal totalPriceDeskRentals) {
        this.desk = desk;
        this.customer = customer;
        this.rentalPlan = rentalPlan;
        this.startPeriodDeskRentals = startPeriodDeskRentals;
        this.endPeriodDeskRentals = endPeriodDeskRentals;
        this.totalPriceDeskRentals = totalPriceDeskRentals;
    }

    // Getters e Setters
    public Integer getIdDeskRentals() {
        return idDeskRentals;
    }

    public void setIdDeskRentals(Integer idDeskRentals) {
        this.idDeskRentals = idDeskRentals;
    }

    public Desk getDesk() {
        return desk;
    }

    public void setDesk(Desk desk) {
        this.desk = desk;
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

    public LocalDateTime getStartPeriodDeskRentals() {
        return startPeriodDeskRentals;
    }

    public void setStartPeriodDeskRentals(LocalDateTime startPeriodDeskRentals) {
        this.startPeriodDeskRentals = startPeriodDeskRentals;
    }

    public LocalDateTime getEndPeriodDeskRentals() {
        return endPeriodDeskRentals;
    }

    public void setEndPeriodDeskRentals(LocalDateTime endPeriodDeskRentals) {
        this.endPeriodDeskRentals = endPeriodDeskRentals;
    }

    public BigDecimal getTotalPriceDeskRentals() {
        return totalPriceDeskRentals;
    }

    public void setTotalPriceDeskRentals(BigDecimal totalPriceDeskRentals) {
        this.totalPriceDeskRentals = totalPriceDeskRentals;
    }
}