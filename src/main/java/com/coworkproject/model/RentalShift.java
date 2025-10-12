package com.coworkproject.model;

import jakarta.persistence.*;
import java.time.LocalTime;

@Entity
@Table(name = "rentalShifts")
public class RentalShift {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`idRentalShifts`")
    private Integer idRentalShifts;

    @Column(name = "`nameRentalShifts`")
    private String nameRentalShifts;

    @Column(name = "`descriptionRentalShifts`")
    private String descriptionRentalShifts;

    @Column(name = "`startTimeRentalShifts`")
    private LocalTime startTimeRentalShifts;

    @Column(name = "`endTimeRentalShifts`")
    private LocalTime endTimeRentalShifts;

    // Construtores
    public RentalShift() {}

    public RentalShift(String nameRentalShifts, String descriptionRentalShifts,
                       LocalTime startTimeRentalShifts, LocalTime endTimeRentalShifts) {
        this.nameRentalShifts = nameRentalShifts;
        this.descriptionRentalShifts = descriptionRentalShifts;
        this.startTimeRentalShifts = startTimeRentalShifts;
        this.endTimeRentalShifts = endTimeRentalShifts;
    }

    // Getters e Setters
    public Integer getIdRentalShifts() {
        return idRentalShifts;
    }

    public void setIdRentalShifts(Integer idRentalShifts) {
        this.idRentalShifts = idRentalShifts;
    }

    public String getNameRentalShifts() {
        return nameRentalShifts;
    }

    public void setNameRentalShifts(String nameRentalShifts) {
        this.nameRentalShifts = nameRentalShifts;
    }

    public String getDescriptionRentalShifts() {
        return descriptionRentalShifts;
    }

    public void setDescriptionRentalShifts(String descriptionRentalShifts) {
        this.descriptionRentalShifts = descriptionRentalShifts;
    }

    public LocalTime getStartTimeRentalShifts() {
        return startTimeRentalShifts;
    }

    public void setStartTimeRentalShifts(LocalTime startTimeRentalShifts) {
        this.startTimeRentalShifts = startTimeRentalShifts;
    }

    public LocalTime getEndTimeRentalShifts() {
        return endTimeRentalShifts;
    }

    public void setEndTimeRentalShifts(LocalTime endTimeRentalShifts) {
        this.endTimeRentalShifts = endTimeRentalShifts;
    }
}