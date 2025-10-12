package com.coworkproject.model;

import jakarta.persistence.*;

@Entity
@Table(name = "rentalCategories")
public class RentalCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`idRentalCategories`")
    private Integer idRentalCategories;

    @Column(name = "`nameRentalCategories`")
    private String nameRentalCategories;

    @Column(name = "`baseDurationInDaysRentalCategories`")
    private Integer baseDurationInDaysRentalCategories;

    // Construtores
    public RentalCategory() {}

    public RentalCategory(String nameRentalCategories, Integer baseDurationInDaysRentalCategories) {
        this.nameRentalCategories = nameRentalCategories;
        this.baseDurationInDaysRentalCategories = baseDurationInDaysRentalCategories;
    }

    // Getters e Setters
    public Integer getIdRentalCategories() {
        return idRentalCategories;
    }

    public void setIdRentalCategories(Integer idRentalCategories) {
        this.idRentalCategories = idRentalCategories;
    }

    public String getNameRentalCategories() {
        return nameRentalCategories;
    }

    public void setNameRentalCategories(String nameRentalCategories) {
        this.nameRentalCategories = nameRentalCategories;
    }

    public Integer getBaseDurationInDaysRentalCategories() {
        return baseDurationInDaysRentalCategories;
    }

    public void setBaseDurationInDaysRentalCategories(Integer baseDurationInDaysRentalCategories) {
        this.baseDurationInDaysRentalCategories = baseDurationInDaysRentalCategories;
    }
}