package com.campus.pulse.controller;

import com.campus.pulse.model.User;
import com.campus.pulse.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Username already exists");
        }

        // --- NEW LOGIC: ADMINS NEED APPROVAL ---
        if ("ADMIN".equals(user.getRole())) {
            user.setApproved(false); // Make them wait for Principal
        } else {
            user.setApproved(true);  // Students/Staff are auto-approved
        }
        // ---------------------------------------

        return ResponseEntity.ok(userRepository.save(user));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User loginRequest) {
        // FIX: Added .orElse(null) to handle the Optional return type
        User user = userRepository.findByUsername(loginRequest.getUsername()).orElse(null);

        if (user == null || !user.getPassword().equals(loginRequest.getPassword())) {
            return ResponseEntity.status(401).body(java.util.Collections.singletonMap("message", "Invalid Credentials"));
        }

        // --- ADMIN STATUS CHECK ---
        if ("ADMIN".equals(user.getRole())) {
            if (!user.isApproved()) {
                if (user.getRejectionReason() != null && !user.getRejectionReason().trim().isEmpty()) {
                    return ResponseEntity.status(403)
                            .body(java.util.Collections.singletonMap("message", "Application Rejected by Principal: " + user.getRejectionReason()));
                } else {
                    return ResponseEntity.status(403)
                            .body(java.util.Collections.singletonMap("message", "Waiting for Principal Approval. Access Denied."));
                }
            }
        }
        // --------------------------

        return ResponseEntity.ok(java.util.Collections.singletonMap("user", user));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> payload) {
        String username = payload.get("username");
        String pin = payload.get("recoveryPin");
        String newPass = payload.get("newPassword");

        Optional<User> userOpt = userRepository.findByUsername(username);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (user.getRecoveryPin() != null && user.getRecoveryPin().equals(pin)) {
                user.setPassword(newPass);
                userRepository.save(user);
                return ResponseEntity.ok("Password updated");
            }
        }
        return ResponseEntity.badRequest().body("Invalid details");
    }
}