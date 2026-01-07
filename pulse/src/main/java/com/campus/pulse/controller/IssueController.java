package com.campus.pulse.controller;

import com.campus.pulse.model.Issue;
import com.campus.pulse.model.IssueStatus;
import com.campus.pulse.repository.IssueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;
import java.util.List;

@RestController
@RequestMapping("/api/issues")
@CrossOrigin(origins = "*")
public class IssueController {

    @Autowired
    private IssueRepository issueRepository;

    @GetMapping
    public List<Issue> getAllIssues() {
        return issueRepository.findAllByOrderByIdDesc();
    }

    @PostMapping("/report")
    public ResponseEntity<?> reportIssue(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("location") String location,
            @RequestParam("category") String category,
            @RequestParam(value = "occurrenceDate", required = false) String occurrenceDate,
            @RequestParam("targetDepartment") String targetDepartment,
            @RequestParam(value = "manualPriority", required = false) String manualPriority,
            @RequestParam(value = "image", required = false) MultipartFile image
    ) {
        try {
            Issue issue = new Issue();
            int randomNum = (int)((Math.random() * 90000) + 10000);
            issue.setReferenceId("ID" + randomNum);

            issue.setTitle(title);
            issue.setDescription(description);
            issue.setLocation(location);
            issue.setCategory(category);
            issue.setTargetDepartment(targetDepartment);

            if ("General".equalsIgnoreCase(targetDepartment)) {
                issue.setHodApproved(true);
            } else {
                issue.setHodApproved(false);
            }

            String todayFormatted = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("MMM dd, HH:mm"));
            issue.setTimestamp(todayFormatted);

            issue.setOccurrenceDate((occurrenceDate == null || occurrenceDate.isEmpty())
                    ? java.time.LocalDate.now().toString()
                    : occurrenceDate);

            issue.setUpvotes(0);
            issue.setStatus(IssueStatus.OPEN);

            if (manualPriority != null && !manualPriority.isEmpty() && !"AUTO".equalsIgnoreCase(manualPriority)) {
                issue.setPriority(manualPriority);
            } else {
                String text = (title + " " + description).toLowerCase();
                if (text.contains("fire") || text.contains("smoke") || text.contains("danger") || text.contains("leak")) {
                    issue.setPriority("CRITICAL");
                } else if (text.contains("wifi") || text.contains("ac") || text.contains("fan") || text.contains("broken")) {
                    issue.setPriority("Medium");
                } else {
                    issue.setPriority("Low");
                }
            }

            // --- THIS IS THE CRITICAL FIX ---
            // Only set image if one is uploaded. Do NOT add an 'else' block for placeholders.
            if (image != null && !image.isEmpty()) {
                String base64Image = Base64.getEncoder().encodeToString(image.getBytes());
                issue.setImageUrl("data:" + image.getContentType() + ";base64," + base64Image);
            }
            // The 'else' block that was here is now DELETED.
            // This ensures issue.imageUrl remains NULL if no file is sent.

            return ResponseEntity.ok(issueRepository.save(issue));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    // --- HOD ASSIGNS ISSUE TO ADMIN ---
    @PutMapping("/{id}/assign")
    public ResponseEntity<?> assignIssue(@PathVariable Long id) {
        return issueRepository.findById(id).map(issue -> {
            issue.setHodApproved(true);
            issueRepository.save(issue);
            return ResponseEntity.ok("Assigned");
        }).orElse(ResponseEntity.notFound().build());
    }

    // ... inside IssueController.java

    // 1. ADMIN ACTION: MARK IN PROGRESS
    @PutMapping("/{id}/inprogress")
    public ResponseEntity<?> markInProgress(@PathVariable Long id, @RequestParam("comment") String comment) {
        return issueRepository.findById(id).map(issue -> {

            // LOGIC: If General, Student sees it immediately.
            // If Departmental, it goes to "ADMIN_IN_PROGRESS" (Hidden from Student, Visible to HOD)
            if ("General".equalsIgnoreCase(issue.getTargetDepartment())) {
                issue.setStatus(IssueStatus.IN_PROGRESS);
            } else {
                issue.setStatus(IssueStatus.ADMIN_IN_PROGRESS);
            }

            issue.setAdminComment(comment);
            issueRepository.save(issue);
            return ResponseEntity.ok("In Progress");
        }).orElse(ResponseEntity.notFound().build());
    }

    // 2. ADMIN ACTION: RESOLVE ISSUE
    @PutMapping("/{id}/resolve")
    public ResponseEntity<?> resolveIssue(@PathVariable Long id, @RequestParam("comment") String comment) {
        return issueRepository.findById(id).map(issue -> {

            // LOGIC: If General, Student sees it immediately.
            // If Departmental, it goes to "ADMIN_RESOLVED" (Hidden from Student, Visible to HOD)
            if ("General".equalsIgnoreCase(issue.getTargetDepartment())) {
                issue.setStatus(IssueStatus.RESOLVED);
            } else {
                issue.setStatus(IssueStatus.ADMIN_RESOLVED);
            }

            issue.setAdminComment(comment);
            issueRepository.save(issue);
            return ResponseEntity.ok("Resolved");
        }).orElse(ResponseEntity.notFound().build());
    }

    // 3. HOD ACTION: PASS INFO TO STUDENT (The Bridge)
    @PutMapping("/{id}/pass")
    public ResponseEntity<?> passInfoToStudent(@PathVariable Long id, @RequestParam("hodComment") String hodComment) {
        return issueRepository.findById(id).map(issue -> {

            // HOD APPROVES "START WORK"
            if (issue.getStatus() == IssueStatus.ADMIN_IN_PROGRESS) {
                issue.setStatus(IssueStatus.IN_PROGRESS); // Now Student sees "Working"
            }
            // HOD APPROVES "FIX"
            else if (issue.getStatus() == IssueStatus.ADMIN_RESOLVED) {
                issue.setStatus(IssueStatus.RESOLVED); // Now Student sees "Resolved"
            }

            // Append HOD comment
            String existingComment = issue.getAdminComment() != null ? issue.getAdminComment() : "";
            issue.setAdminComment(existingComment + " | [HOD]: " + hodComment);

            issueRepository.save(issue);
            return ResponseEntity.ok("Info Passed");
        }).orElse(ResponseEntity.notFound().build());
    }

    // --- STUDENT ACTIONS ---
    @PutMapping("/{id}/verify")
    public ResponseEntity<?> verifyIssue(@PathVariable Long id) {
        return issueRepository.findById(id).map(issue -> {
            issue.setVerified(true);
            issueRepository.save(issue);
            return ResponseEntity.ok("Verified");
        }).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/reopen")
    public ResponseEntity<?> reopenIssue(
            @PathVariable Long id,
            @RequestParam("reason") String reason,
            @RequestParam(value = "proofImage", required = false) MultipartFile proofImage // <--- NEW OPTIONAL PARAM
    ) {
        return issueRepository.findById(id).map(issue -> {
            issue.setStatus(IssueStatus.OPEN);
            issue.setVerified(false);

            // Append the reopen reason to the description history
            String timestamp = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"));
            issue.setDescription(issue.getDescription() + "\n\n[REOPENED @ " + timestamp + "]: " + reason);

            // LOGIC: If a new proof image is provided, overwrite the old one.
            // If not provided, keep the old image.
            if (proofImage != null && !proofImage.isEmpty()) {
                try {
                    String base64Image = Base64.getEncoder().encodeToString(proofImage.getBytes());
                    issue.setImageUrl("data:" + proofImage.getContentType() + ";base64," + base64Image);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            issueRepository.save(issue);
            return ResponseEntity.ok("Reopened");
        }).orElse(ResponseEntity.notFound().build());
    }
}