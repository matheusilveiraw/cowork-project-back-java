package com.coworkproject.model;

import jakarta.persistence.*;

@Entity
@Table(name = "rooms")
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idRooms")
    private Integer idRooms;

    @Column(name = "numberRooms", nullable = false, unique = true)
    private Integer numberRooms;

    @Column(name = "nameRooms", length = 100)
    private String nameRooms;

    // Construtores
    public Room() {}

    public Room(Integer numberRooms, String nameRooms) {
        this.numberRooms = numberRooms;
        this.nameRooms = nameRooms;
    }

    // Getters e Setters
    public Integer getIdRooms() {
        return idRooms;
    }

    public void setIdRooms(Integer idRooms) {
        this.idRooms = idRooms;
    }

    public Integer getNumberRooms() {
        return numberRooms;
    }

    public void setNumberRooms(Integer numberRooms) {
        this.numberRooms = numberRooms;
    }

    public String getNameRooms() {
        return nameRooms;
    }

    public void setNameRooms(String nameRooms) {
        this.nameRooms = nameRooms;
    }

    // toString
    @Override
    public String toString() {
        return "Room{" +
                "idRooms=" + idRooms +
                ", numberRooms=" + numberRooms +
                ", nameRooms='" + nameRooms + '\'' +
                '}';
    }
}