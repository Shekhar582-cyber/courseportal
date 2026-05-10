package com.example.courseportal.controller;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.courseportal.entity.User;
import com.example.courseportal.service.UserService;

@CrossOrigin
@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService service;

    // ── GET ALL USERS (Admin only) ──────────────────────────────
    @GetMapping("/all")
    public ResponseEntity<?> getAllUsers(@RequestHeader(value = "X-User-Role", required = false) String role) {
        if (!"ADMIN".equalsIgnoreCase(role)) {
            return ResponseEntity.status(403).body(Map.of("success", false, "message", "Access denied. Admins only."));
        }
        return ResponseEntity.ok(service.getAllUsers());
    }

    // ── REGISTER ────────────────────────────────────────────────
    @PostMapping("/add")
    public User addUser(@RequestBody User user) {
        if (user.getRole() == null || user.getRole().isBlank()) {
            user.setRole("STUDENT");
        }
        return service.saveUser(user);
    }

    // ── LOGIN ───────────────────────────────────────────────────
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        User found = service.login(user.getEmail(), user.getPassword());
        if (found != null) {
            Map<String, Object> resp = new HashMap<>();
            resp.put("success", true);
            resp.put("id",    found.getId());
            resp.put("name",  found.getName());
            resp.put("email", found.getEmail());
            resp.put("role",  found.getRole() != null ? found.getRole() : "STUDENT");
            resp.put("redirectUrl", "dashboard.html");
            return ResponseEntity.ok(resp);
        }
        return ResponseEntity.status(401).body(Map.of("success", false, "message", "Invalid email or password"));
    }

    // ── GOOGLE LOGIN ────────────────────────────────────────────
    @PostMapping(value = "/google-login", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> googleLogin(@RequestBody Map<String, String> body) {
        try {
            String idToken = body.get("idToken");
            String email = body.get("email");
            String name = body.get("name");

            if (idToken == null || idToken.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Missing ID token"));
            }

            if (email == null || email.isBlank() || name == null || name.isBlank()) {
                Map<String, String> tokenClaims = extractGoogleClaims(idToken);
                if ((email == null || email.isBlank()) && tokenClaims.containsKey("email")) {
                    email = tokenClaims.get("email");
                }
                if ((name == null || name.isBlank()) && tokenClaims.containsKey("name")) {
                    name = tokenClaims.get("name");
                }
            }

            if (email == null || email.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Google account email is missing"));
            }

            User user = service.findOrCreateGoogleUser(name, email);
            if (user == null) {
                return ResponseEntity.status(500).body(Map.of("success", false, "message", "Unable to sign in with Google"));
            }

            Map<String, Object> resp = new HashMap<>();
            resp.put("success", true);
            resp.put("id", user.getId());
            resp.put("name", user.getName());
            resp.put("email", user.getEmail());
            resp.put("role", user.getRole() != null ? user.getRole() : "STUDENT");
            resp.put("redirectUrl", "dashboard.html");
            return ResponseEntity.ok(resp);
        } catch (Exception ex) {
            return ResponseEntity.status(500).body(Map.of(
                    "success", false,
                    "message", "Google sign-in failed on server: " + ex.getClass().getSimpleName()
            ));
        }
    }

    private Map<String, String> extractGoogleClaims(String idToken) {
        Map<String, String> claims = new HashMap<>();
        try {
            String[] parts = idToken.split("\\.");
            if (parts.length < 2) {
                return claims;
            }
            byte[] decoded = Base64.getUrlDecoder().decode(parts[1]);
            String payload = new String(decoded, StandardCharsets.UTF_8);
            String email = extractJsonValue(payload, "email");
            String name = extractJsonValue(payload, "name");
            if (email != null && !email.isBlank()) {
                claims.put("email", email);
            }
            if (name != null && !name.isBlank()) {
                claims.put("name", name);
            }
        } catch (Exception ignored) {
        }
        return claims;
    }

    private String extractJsonValue(String json, String key) {
        String marker = "\"" + key + "\":";
        int start = json.indexOf(marker);
        if (start < 0) {
            return null;
        }
        int valueStart = json.indexOf('"', start + marker.length());
        if (valueStart < 0) {
            return null;
        }
        int valueEnd = json.indexOf('"', valueStart + 1);
        if (valueEnd < 0) {
            return null;
        }
        return json.substring(valueStart + 1, valueEnd);
    }

    // ── DELETE ──────────────────────────────────────────────────
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteUser(
            @PathVariable int id,
            @RequestHeader(value = "X-User-Role", required = false) String role) {
        if (!"ADMIN".equalsIgnoreCase(role)) {
            return ResponseEntity.status(403).body(Map.of("success", false, "message", "Access denied. Admins only."));
        }
        service.deleteUser(id);
        return ResponseEntity.ok().build();
    }

    // ── UPDATE PROFILE ──────────────────────────────────────────
    @PutMapping("/update")
    public User updateUser(@RequestBody User user) {
        return service.updateUser(user);
    }

    @PutMapping("/role")
    public ResponseEntity<?> updateRole(
            @RequestBody Map<String, String> body,
            @RequestHeader(value = "X-User-Role", required = false) String role) {
        if (!"ADMIN".equalsIgnoreCase(role)) {
            return ResponseEntity.status(403).body(Map.of("success", false, "message", "Access denied. Admins only."));
        }
        int id = Integer.parseInt(body.getOrDefault("id", "0"));
        String newRole = body.getOrDefault("role", "STUDENT").toUpperCase();
        if (!List.of("STUDENT", "INSTRUCTOR", "ADMIN").contains(newRole)) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Invalid role"));
        }
        return service.getAllUsers().stream()
                .filter(u -> u.getId() == id)
                .findFirst()
                .<ResponseEntity<?>>map(u -> {
                    u.setRole(newRole);
                    return ResponseEntity.ok(Map.of("success", true, "user", service.updateUser(u)));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // ── CHANGE PASSWORD ─────────────────────────────────────────
    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, String> body) {
        String email           = body.get("email");
        String currentPassword = body.get("currentPassword");
        String newPassword     = body.get("newPassword");

        if (email == null || currentPassword == null || newPassword == null || newPassword.length() < 6) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Invalid request"));
        }
        User found = service.login(email, currentPassword);
        if (found == null) {
            return ResponseEntity.status(401).body(Map.of("success", false, "message", "Current password is incorrect"));
        }
        found.setPassword(newPassword);
        service.updateUser(found);
        return ResponseEntity.ok(Map.of("success", true, "message", "Password changed successfully"));
    }

    // ── GET USER BY ID ──────────────────────────────────────────
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable int id) {
        return service.getAllUsers().stream()
                .filter(u -> u.getId() == id)
                .findFirst()
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ── THEME PREFERENCE ────────────────────────────────────────
    @PutMapping("/theme")
    public ResponseEntity<?> updateTheme(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String theme = body.get("theme");
        if (email == null || theme == null) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Invalid request"));
        }
        User user = service.findByEmail(email);
        if (user == null) return ResponseEntity.notFound().build();
        user.setTheme(theme);
        service.updateUser(user);
        return ResponseEntity.ok(Map.of("success", true, "theme", theme));
    }
}
