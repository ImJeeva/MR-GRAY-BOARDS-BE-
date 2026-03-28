package com.invoice.invoice.service;

import com.invoice.invoice.entity.Admin;
import com.invoice.invoice.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    @PostConstruct
    public void initDefaultAdmin() {
        if (adminRepository.findByUsername("admin").isEmpty()) {
            Admin defaultAdmin = new Admin();
            defaultAdmin.setUsername("admin");
            defaultAdmin.setPassword(passwordEncoder.encode("admin123"));
            defaultAdmin.setEmail("admin@company.com");
            defaultAdmin.setPhone("9999999999");
            defaultAdmin.setActive(true);
            adminRepository.save(defaultAdmin);
        }
    }
    
    public Optional<Admin> findByUsername(String username) {
        return adminRepository.findByUsername(username);
    }
    
    public boolean validatePassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
    
    public String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }
    
    public Optional<Admin> findByEmail(String email) {
        return adminRepository.findByEmail(email);
    }
    
    public void updatePassword(Long id, String newPassword) {
        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Admin not found"));
        admin.setPassword(passwordEncoder.encode(newPassword));
        adminRepository.save(admin);
    }
}
