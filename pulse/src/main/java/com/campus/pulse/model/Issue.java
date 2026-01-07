package com.campus.pulse.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Data
@Table(name = "issues")
public class Issue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(length = 1000)
    private String description;

    private String location;

    // --- UPDATED: Allow Huge Strings for Image Data ---
    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    private IssueStatus status = IssueStatus.OPEN;

    private String priority;

    // --- Admin Comment Field ---
    private String adminComment;

    // --- NEW: User Verification ---
    @Column(columnDefinition = "boolean default false")
    private boolean verified = false;

    // --- NEW: Unique Reference ID ---
    @Column(unique = true)
    private String referenceId;

    // --- NEW: HOD WORKFLOW FIELDS ---

    // Which department needs to approve this? (e.g., "CSE", "ECE")
    private String targetDepartment;

    // Has the HOD clicked "Assign"?
    @Column(columnDefinition = "boolean default false")
    private boolean hodApproved = false;

    // --- NEW: When the issue actually happened ---
    private String occurrenceDate;

    private int upvotes = 0;

    private String user = "Student";

    private String timestamp;

    // Inside com.campus.pulse.model.Issue.java

    @Column(name = "category")
    private String category;

    // Add Getter and Setter
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    @PrePersist
    public void setupDate() {
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMM dd, HH:mm"));
    }
}