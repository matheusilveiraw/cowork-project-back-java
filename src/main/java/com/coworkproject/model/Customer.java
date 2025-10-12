package com.coworkproject.model;

import jakarta.persistence.*;

@Entity
@Table(name = "customers")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`idCustomers`")
    private Integer idCustomers;

    @Column(name = "`nameCustomers`")
    private String nameCustomers;

    @Column(name = "`emailCustomers`")
    private String emailCustomers;

    @Column(name = "`phoneCustomers`")
    private String phoneCustomers;

    @Column(name = "`addressCustomers`")
    private String addressCustomers;

    @Column(name = "`cpfCustomers`")
    private String cpfCustomers;

    // Getters e Setters (mantenha como estava)
    public Integer getIdCustomers() { return idCustomers; }
    public void setIdCustomers(Integer idCustomers) { this.idCustomers = idCustomers; }
    public String getNameCustomers() { return nameCustomers; }
    public void setNameCustomers(String nameCustomers) { this.nameCustomers = nameCustomers; }
    public String getEmailCustomers() { return emailCustomers; }
    public void setEmailCustomers(String emailCustomers) { this.emailCustomers = emailCustomers; }
    public String getPhoneCustomers() { return phoneCustomers; }
    public void setPhoneCustomers(String phoneCustomers) { this.phoneCustomers = phoneCustomers; }
    public String getAddressCustomers() { return addressCustomers; }
    public void setAddressCustomers(String addressCustomers) { this.addressCustomers = addressCustomers; }
    public String getCpfCustomers() { return cpfCustomers; }
    public void setCpfCustomers(String cpfCustomers) { this.cpfCustomers = cpfCustomers; }
}