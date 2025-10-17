package com.coworkproject.model;

import jakarta.persistence.*;

@Entity
@Table(name = "stands")
public class Stand {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idStands")
    private Integer idStands;

    @Column(name = "numberStands", nullable = false, unique = true)
    private Integer numberStands;

    @Column(name = "nameStands", length = 100)
    private String nameStands;

    // Construtores
    public Stand() {}

    public Stand(Integer numberStands, String nameStands) {
        this.numberStands = numberStands;
        this.nameStands = nameStands;
    }

    // Getters e Setters
    public Integer getIdStands() {
        return idStands;
    }

    public void setIdStands(Integer idStands) {
        this.idStands = idStands;
    }

    public Integer getNumberStands() {
        return numberStands;
    }

    public void setNumberStands(Integer numberStands) {
        this.numberStands = numberStands;
    }

    public String getNameStands() {
        return nameStands;
    }

    public void setNameStands(String nameStands) {
        this.nameStands = nameStands;
    }

    // toString
    @Override
    public String toString() {
        return "Stand{" +
                "idStands=" + idStands +
                ", numberStands=" + numberStands +
                ", nameStands='" + nameStands + '\'' +
                '}';
    }
}