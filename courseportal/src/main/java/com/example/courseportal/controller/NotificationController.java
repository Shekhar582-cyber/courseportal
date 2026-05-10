package com.example.courseportal.controller;

import com.example.courseportal.entity.Notification;
import com.example.courseportal.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/notifications")
public class NotificationController {
    @Autowired private NotificationRepository repo;

    @GetMapping("/user")
    public ResponseEntity<?> byUser(@RequestParam int userId, @RequestParam(defaultValue = "STUDENT") String role) {
        return ResponseEntity.ok(repo.findByUserIdOrTargetRoleOrderByCreatedAtDesc(userId, role.toUpperCase()));
    }

    @PostMapping("/send")
    public ResponseEntity<?> send(
            @RequestBody Notification notification,
            @RequestHeader(value = "X-User-Role", required = false) String role) {
        if (!"ADMIN".equalsIgnoreCase(role)) {
            return ResponseEntity.status(403).body(Map.of("success", false, "message", "Admins only."));
        }
        notification.setCreatedAt(LocalDateTime.now());
        return ResponseEntity.ok(repo.save(notification));
    }

    @PutMapping("/read/{id}")
    public ResponseEntity<?> read(@PathVariable int id) {
        return repo.findById(id).map(n -> {
            n.setReadStatus(true);
            return ResponseEntity.ok(repo.save(n));
        }).orElse(ResponseEntity.notFound().build());
    }
}
