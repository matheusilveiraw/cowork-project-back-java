package com.coworkproject.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "roomRentals")
public class RoomRental {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idRoomRentals")
    private Integer idRoomRentals;

    @ManyToOne
    @JoinColumn(name = "idRooms", nullable = false)
    private Room room;

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
    public RoomRental() {}

    public RoomRental(Room room, Customer customer,
                      LocalDateTime startPeriodAreaRentals, LocalDateTime endPeriodAreaRentals,
                      BigDecimal totalPriceStandRentals) {
        this.room = room;
        this.customer = customer;
        this.startPeriodAreaRentals = startPeriodAreaRentals;
        this.endPeriodAreaRentals = endPeriodAreaRentals;
        this.totalPriceStandRentals = totalPriceStandRentals;
    }

    // Getters e Setters
    public Integer getIdRoomRentals() {
        return idRoomRentals;
    }

    public void setIdRoomRentals(Integer idRoomRentals) {
        this.idRoomRentals = idRoomRentals;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
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
        return "RoomRental{" +
                "idRoomRentals=" + idRoomRentals +
                ", room=" + room +
                ", customer=" + customer +
                ", startPeriodAreaRentals=" + startPeriodAreaRentals +
                ", endPeriodAreaRentals=" + endPeriodAreaRentals +
                ", totalPriceStandRentals=" + totalPriceStandRentals +
                '}';
    }
}