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
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonBackReference;
@Entity
@Table(name = "Staff")
public class Staff {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "staffId")
    private int staffId;

    @NotBlank
    @Column(name = "staffName", nullable = false)
    private String staffName;

    @NotBlank
    @Column(name = "department", nullable = false)
    private String department;

    @Min(1)
    @Max(5)
    @Column(name = "performanceRating")
    private int performanceRating;

    @ManyToOne()
    @JoinColumn(name = "hotelId")
    @JsonIgnoreProperties("staffList")
    private Hotel hotel;

    public Staff() {}

    public Staff(String staffName, String department, Hotel hotel) {
        this.staffName = staffName;
        this.department = department;
        this.performanceRating = 3;
        this.hotel = hotel;
    }

    public Staff(String staffName, String department) {
        this.staffName = staffName;
        this.department = department;
        this.performanceRating = 3;
        this.hotel = null;
    }

    
    // Getters and Setters
    public int getStaffId() {
        return staffId;
    }

    public void setStaffId(int staffId) {
        this.staffId = staffId;
    }

    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public int getPerformanceRating() {
        return performanceRating;
    }

    public void setPerformanceRating(int performanceRating) {
        this.performanceRating = performanceRating;
    }

    public Hotel getHotel() {
        return hotel;
    }

    public void setHotel(Hotel hotel) {
        this.hotel = hotel;
    }

    @Override
    public String toString() {
        return staffId + " - " + staffName + " (" + department + ") - Rating: " + performanceRating;
    }
}