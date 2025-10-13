package com.coworkproject.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "rentalPlans")
public class RentalPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`idRentalPlans`")
    private Integer idRentalPlans;

    @ManyToOne
    @JoinColumn(name = "`idRentalCategories`", referencedColumnName = "`idRentalCategories`")
    private RentalCategory rentalCategory;

    @ManyToOne
    @JoinColumn(name = "`idRentalShifts`", referencedColumnName = "`idRentalShifts`")
    private RentalShift rentalShift;

    @Column(name = "`planNameRentalPlans`")
    private String planNameRentalPlans;

    @Column(name = "`priceRentalPlans`")
    private BigDecimal priceRentalPlans;

    // Construtores
    public RentalPlan() {}

    public RentalPlan(RentalCategory rentalCategory, RentalShift rentalShift,
                      String planNameRentalPlans, BigDecimal priceRentalPlans) {
        this.rentalCategory = rentalCategory;
        this.rentalShift = rentalShift;
        this.planNameRentalPlans = planNameRentalPlans;
        this.priceRentalPlans = priceRentalPlans;
    }

    // Getters e Setters
    public Integer getIdRentalPlans() {
        return idRentalPlans;
    }

    public void setIdRentalPlans(Integer idRentalPlans) {
        this.idRentalPlans = idRentalPlans;
    }

    public RentalCategory getRentalCategory() {
        return rentalCategory;
    }

    public void setRentalCategory(RentalCategory rentalCategory) {
        this.rentalCategory = rentalCategory;
    }

    public RentalShift getRentalShift() {
        return rentalShift;
    }

    public void setRentalShift(RentalShift rentalShift) {
        this.rentalShift = rentalShift;
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