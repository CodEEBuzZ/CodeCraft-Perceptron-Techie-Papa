package com.campus.pulse.model;

public enum IssueStatus {
    OPEN,

    // --- NEW: Intermediate Statuses for HOD Review ---
    ADMIN_IN_PROGRESS, // Admin started work, waiting for HOD to show student
    ADMIN_RESOLVED,    // Admin finished, waiting for HOD to show student

    IN_PROGRESS,       // Official status (Student sees this)
    RESOLVED           // Official status (Student sees this)
}