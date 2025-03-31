package com.example.demo;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.sql.Date;
//import java.util.Date;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.fasterxml.jackson.annotation.JsonBackReference;

//Customer entity class - Model class
@Entity
@Table(name="User")
public class User {
	

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @NotBlank(message = "Username is mandatory")
    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @NotBlank(message = "Password is mandatory")
    @Column(name = "password", nullable = false)
    private String password;

    @NotBlank(message = "Full Name is mandatory")
    @Column(name = "fullName", nullable = false)
    private String fullName;

    @NotBlank(message = "Role is mandatory")
    @Column(name = "role", nullable = false)
    private String role;
    
    public static User createWithEncodedPassword(String username, String rawPassword, String fullName, String role, PasswordEncoder encoder) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(encoder.encode(rawPassword)); // 🔐 Hash password here
        user.setFullName(fullName);
        user.setRole(role);
        return user;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}