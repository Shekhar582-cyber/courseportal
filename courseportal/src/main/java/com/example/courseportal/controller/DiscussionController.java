package com.example.courseportal.controller;

import com.example.courseportal.entity.Discussion;
import com.example.courseportal.repository.DiscussionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/discussions")
public class DiscussionController {

    @Autowired
    private DiscussionRepository repo;

    @GetMapping("/lesson/{lessonId}")
    public List<Discussion> getByLesson(@PathVariable int lessonId) {
        return repo.findByLessonIdOrderByCreatedAtAsc(lessonId);
    }

    @PostMapping("/post")
    public ResponseEntity<?> post(@RequestBody Discussion d) {
        if (d.getMessage() == null || d.getMessage().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Message cannot be empty"));
        }
        d.setCreatedAt(LocalDateTime.now());
        return ResponseEntity.ok(repo.save(d));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable int id,
                                    @RequestHeader(value = "X-User-Id", required = false) Integer userId,
                                    @RequestHeader(value = "X-User-Role", required = false) String role) {
        return repo.findById(id).map(d -> {
            if (!"ADMIN".equalsIgnoreCase(role) && (userId == null || userId != d.getUserId())) {
                return ResponseEntity.status(403).body(Map.of("success", false, "message", "Not allowed"));
            }
            repo.delete(d);
            return ResponseEntity.ok(Map.of("success", true));
        }).orElse(ResponseEntity.notFound().build());
    }
}
