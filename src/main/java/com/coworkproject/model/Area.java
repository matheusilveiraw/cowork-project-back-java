package com.coworkproject.model;

import jakarta.persistence.*;

@Entity
@Table(name = "areas")
public class Area {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idAreas")
    private Integer idAreas;

    @Column(name = "numberAreas", nullable = false, unique = true)
    private Integer numberAreas;

    @Column(name = "nameAreas", length = 100)
    private String nameAreas;

    // Construtores
    public Area() {}

    public Area(Integer numberAreas, String nameAreas) {
        this.numberAreas = numberAreas;
        this.nameAreas = nameAreas;
    }

    // Getters e Setters
    public Integer getIdAreas() {
        return idAreas;
    }

    public void setIdAreas(Integer idAreas) {
        this.idAreas = idAreas;
    }

    public Integer getNumberAreas() {
        return numberAreas;
    }

    public void setNumberAreas(Integer numberAreas) {
        this.numberAreas = numberAreas;
    }

    public String getNameAreas() {
        return nameAreas;
    }

    public void setNameAreas(String nameAreas) {
        this.nameAreas = nameAreas;
    }

    // toString
    @Override
    public String toString() {
        return "Area{" +
                "idAreas=" + idAreas +
                ", numberAreas=" + numberAreas +
                ", nameAreas='" + nameAreas + '\'' +
                '}';
    }
}