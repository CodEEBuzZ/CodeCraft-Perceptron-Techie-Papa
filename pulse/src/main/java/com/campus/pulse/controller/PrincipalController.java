package com.campus.pulse.controller;

import com.campus.pulse.model.User;
import com.campus.pulse.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/principal")
@CrossOrigin(origins = "*")
public class PrincipalController {

    @Autowired
    private UserRepository userRepository;

    // 1. GET PENDING (Not approved AND No rejection reason)
    @GetMapping("/pending")
    public List<User> getPending() {
        return userRepository.findAll().stream()
                .filter(u -> "ADMIN".equals(u.getRole()) && !u.isApproved() && u.getRejectionReason() == null)
                .collect(Collectors.toList());
    }

    // 2. GET ACCEPTED (Approved)
    @GetMapping("/accepted")
    public List<User> getAccepted() {
        return userRepository.findAll().stream()
                .filter(u -> "ADMIN".equals(u.getRole()) && u.isApproved())
                .collect(Collectors.toList());
    }

    // 3. GET REJECTED (Has a rejection reason)
    @GetMapping("/rejected")
    public List<User> getRejected() {
        return userRepository.findAll().stream()
                .filter(u -> "ADMIN".equals(u.getRole()) && u.getRejectionReason() != null)
                .collect(Collectors.toList());
    }

    // ACTION: APPROVE
    @PutMapping("/approve/{id}")
    public ResponseEntity<?> approveAdmin(@PathVariable Long id) {
        return userRepository.findById(id).map(user -> {
            user.setApproved(true);
            user.setRejectionReason(null); // Clear any previous rejection
            userRepository.save(user);
            return ResponseEntity.ok("Approved");
        }).orElse(ResponseEntity.notFound().build());
    }

    // ACTION: REJECT (With Comment)
    @PutMapping("/reject/{id}")
    public ResponseEntity<?> rejectAdmin(@PathVariable Long id, @RequestParam("reason") String reason) {
        return userRepository.findById(id).map(user -> {
            user.setApproved(false);
            user.setRejectionReason(reason); // Save the comment
            userRepository.save(user);
            return ResponseEntity.ok("Rejected");
        }).orElse(ResponseEntity.notFound().build());
    }
}