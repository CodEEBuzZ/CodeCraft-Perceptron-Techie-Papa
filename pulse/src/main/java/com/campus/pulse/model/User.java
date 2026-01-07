package com.campus.pulse.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // --- Login Credentials ---
    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    private String role; // "STUDENT", "STAFF", "ADMIN", "PRINCIPAL", "HOD"
    private String recoveryPin;

    // --- Approval Status ---
    @Column(columnDefinition = "boolean default false")
    private boolean isApproved = false;

    // --- Common Details ---
    private String firstName;
    private String middleName;
    private String lastName;
    private String phoneNumber;

    // --- Student Specific Fields ---
    private String branch;
    private String year;
    private String registrationNumber;
    private String rollNumber;

    // --- Staff / HOD Specific Fields ---
    private String employeeId;
    private String department;

    // --- Principal Rejection Reason ---
    private String rejectionReason;
}