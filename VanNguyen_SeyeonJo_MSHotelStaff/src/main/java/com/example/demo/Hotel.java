package com.example.demo;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.sql.Date;
//import java.util.Date;
import java.util.List;
@Entity
@Table(name = "Hotel")
public class Hotel {

    @Id
    @Pattern(regexp = "^[A-Z][a-z]{3}\\d{4}$", message = "Requied Format: Xxxx1234")
    @Column(name = "hotelId")
    private String hotelId;

    @NotBlank(message = "Name Required")
    @Column(name = "hotelName", nullable = false)
    private String hotelName;

    @Min(value = 1, message = "Rate must be >=1")
    @Max(value = 5, message = "Rate must be <=5")
    @Column(name = "starRating")
    private int starRating;

    @OneToMany(mappedBy = "hotel", fetch = FetchType.LAZY)
    private List<Staff> staffList;

    public Hotel() {}

    public Hotel(String hotelId, String hotelName, int starRating) {
        this.hotelId = hotelId;
        this.hotelName = hotelName;
        this.starRating = starRating;
    }

    // Getters and Setters
    public String getHotelId() {
        return hotelId;
    }

    public void setHotelId(String hotelId) {
        this.hotelId = hotelId;
    }

    public String getHotelName() {
        return hotelName;
    }

    public void setHotelName(String hotelName) {
        this.hotelName = hotelName;
    }

    public int getStarRating() {
        return starRating;
    }

    public void setStarRating(int starRating) {
        this.starRating = starRating;
    }

    public List<Staff> getStaffList() {
        return staffList;
    }

    public void setStaffList(List<Staff> staffList) {
        this.staffList = staffList;
    }

    @Override
    public String toString() {
        return hotelId + " --- " + hotelName + " --- Rating: " + starRating;
    }
}