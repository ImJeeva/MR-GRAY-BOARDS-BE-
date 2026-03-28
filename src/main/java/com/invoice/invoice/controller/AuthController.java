package com.invoice.invoice.controller;

import com.invoice.invoice.entity.Admin;
import com.invoice.invoice.service.AdminService;
import com.invoice.invoice.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AdminService adminService;
    private final EmailService emailService;
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");
        
        if (username == null || password == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Username and password required"));
        }
        
        return adminService.findByUsername(username)
                .map(admin -> {
                    if (adminService.validatePassword(password, admin.getPassword())) {
                        if (!admin.isActive()) {
                            return ResponseEntity.status(403).body(Map.of("error", "Account is disabled"));
                        }
                        Map<String, Object> response = new HashMap<>();
                        response.put("success", true);
                        response.put("username", admin.getUsername());
                        response.put("email", admin.getEmail());
                        response.put("id", admin.getId());
                        return ResponseEntity.ok(response);
                    } else {
                        return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
                    }
                })
                .orElse(ResponseEntity.status(401).body(Map.of("error", "Invalid credentials")));
    }
    
    @GetMapping("/check")
    public ResponseEntity<?> checkAuth() {
        return ResponseEntity.ok(Map.of("authenticated", true));
    }
    
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        
        if (email == null || email.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email is required"));
        }
        
        Optional<Admin> adminOpt = adminService.findByEmail(email);
        
        if (adminOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "No account found with this email"));
        }
        
        Admin admin = adminOpt.get();
        String otp = emailService.generateOtp(email);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "OTP sent to your email");
        response.put("email", email);
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String otp = request.get("otp");
        
        if (email == null || otp == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email and OTP are required"));
        }
        
        boolean isValid = emailService.verifyOtp(email, otp);
        
        if (isValid) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "OTP verified successfully");
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid or expired OTP"));
        }
    }
    
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String newPassword = request.get("newPassword");
        
        if (email == null || newPassword == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email and new password are required"));
        }
        
        if (newPassword.length() < 6) {
            return ResponseEntity.badRequest().body(Map.of("error", "Password must be at least 6 characters"));
        }
        
        Optional<Admin> adminOpt = adminService.findByEmail(email);
        
        if (adminOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Admin not found"));
        }
        
        Admin admin = adminOpt.get();
        adminService.updatePassword(admin.getId(), newPassword);
        emailService.sendPasswordChangedConfirmation(email);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Password reset successfully");
        
        return ResponseEntity.ok(response);
    }
}
