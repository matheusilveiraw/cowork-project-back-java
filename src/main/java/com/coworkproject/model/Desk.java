package com.coworkproject.model;

import jakarta.persistence.*;

@Entity
@Table(name = "desks")
public class Desk {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`idDesks`")
    private Integer idDesks;

    @Column(name = "`numberDesks`", unique = true)
    private Integer numberDesks;

    @Column(name = "`nameDesks`")
    private String nameDesks;

    // Construtores
    public Desk() {}

    public Desk(Integer numberDesks, String nameDesks) {
        this.numberDesks = numberDesks;
        this.nameDesks = nameDesks;
    }

    // Getters e Setters
    public Integer getIdDesks() {
        return idDesks;
    }

    public void setIdDesks(Integer idDesks) {
        this.idDesks = idDesks;
    }

    public Integer getNumberDesks() {
        return numberDesks;
    }

    public void setNumberDesks(Integer numberDesks) {
        this.numberDesks = numberDesks;
    }

    public String getNameDesks() {
        return nameDesks;
    }

    public void setNameDesks(String nameDesks) {
        this.nameDesks = nameDesks;
    }
}