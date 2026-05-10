package com.example.courseportal.controller;

import com.example.courseportal.entity.LessonBookmark;
import com.example.courseportal.repository.LessonBookmarkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/bookmarks")
public class LessonBookmarkController {

    @Autowired
    private LessonBookmarkRepository repo;

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getAll(@PathVariable int userId) {
        return ResponseEntity.ok(repo.findByUserIdOrderByCreatedAtDesc(userId));
    }

    @GetMapping("/check")
    public ResponseEntity<?> check(@RequestParam int userId, @RequestParam int lessonId) {
        return ResponseEntity.ok(Map.of("bookmarked", repo.findByUserIdAndLessonId(userId, lessonId).isPresent()));
    }

    @PostMapping("/toggle")
    @Transactional
    public ResponseEntity<?> toggle(@RequestBody LessonBookmark bm) {
        var existing = repo.findByUserIdAndLessonId(bm.getUserId(), bm.getLessonId());
        if (existing.isPresent()) {
            repo.deleteByUserIdAndLessonId(bm.getUserId(), bm.getLessonId());
            return ResponseEntity.ok(Map.of("bookmarked", false));
        }
        bm.setCreatedAt(LocalDateTime.now());
        repo.save(bm);
        return ResponseEntity.ok(Map.of("bookmarked", true));
    }
}
