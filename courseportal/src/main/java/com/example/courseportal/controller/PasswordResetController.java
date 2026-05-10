package com.example.courseportal.controller;

import com.example.courseportal.entity.PasswordResetToken;
import com.example.courseportal.repository.PasswordResetTokenRepository;
import com.example.courseportal.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@CrossOrigin
@RestController
@RequestMapping("/auth")
public class PasswordResetController {

    @Autowired private PasswordResetTokenRepository tokenRepo;
    @Autowired private UserService userService;

    /**
     * Request password reset — generates a token.
     * In production this would email the link; here we return the token directly
     * so it can be used without an email server.
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        if (email == null || email.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Email is required"));
        }
        var user = userService.findByEmail(email);
        if (user == null) {
            // Don't reveal whether email exists
            return ResponseEntity.ok(Map.of("success", true, "message", "If that email exists, a reset link has been sent."));
        }
        // Delete old tokens for this email
        tokenRepo.deleteByEmail(email);

        String token = UUID.randomUUID().toString();
        PasswordResetToken prt = new PasswordResetToken();
        prt.setEmail(email);
        prt.setToken(token);
        prt.setExpiresAt(LocalDateTime.now().plusHours(1));
        prt.setUsed(false);
        tokenRepo.save(prt);

        // In production: send email with reset link
        // For demo: return token in response
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Reset token generated. Use it within 1 hour.",
                "resetToken", token  // remove in production
        ));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> body) {
        String token = body.get("token");
        String newPassword = body.get("newPassword");

        if (token == null || newPassword == null || newPassword.length() < 6) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Invalid request"));
        }

        var prt = tokenRepo.findByTokenAndUsedFalse(token).orElse(null);
        if (prt == null || prt.getExpiresAt().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Token is invalid or expired"));
        }

        var user = userService.findByEmail(prt.getEmail());
        if (user == null) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "User not found"));
        }

        user.setPassword(newPassword);
        userService.updateUser(user);
        prt.setUsed(true);
        tokenRepo.save(prt);

        return ResponseEntity.ok(Map.of("success", true, "message", "Password reset successfully"));
    }
}
