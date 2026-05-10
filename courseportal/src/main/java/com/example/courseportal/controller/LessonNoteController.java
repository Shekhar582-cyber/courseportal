package com.example.courseportal.controller;

import com.example.courseportal.entity.LessonNote;
import com.example.courseportal.repository.LessonNoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/notes")
public class LessonNoteController {

    @Autowired
    private LessonNoteRepository repo;

    @GetMapping
    public ResponseEntity<?> getNote(@RequestParam int userId, @RequestParam int lessonId) {
        return ResponseEntity.ok(repo.findByUserIdAndLessonId(userId, lessonId).orElse(null));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getAllNotes(@PathVariable int userId) {
        return ResponseEntity.ok(repo.findByUserId(userId));
    }

    @PostMapping("/save")
    public ResponseEntity<?> save(@RequestBody LessonNote note) {
        LessonNote target = repo.findByUserIdAndLessonId(note.getUserId(), note.getLessonId())
                .orElseGet(LessonNote::new);
        target.setUserId(note.getUserId());
        target.setLessonId(note.getLessonId());
        target.setCourseId(note.getCourseId());
        target.setContent(note.getContent());
        target.setUpdatedAt(LocalDateTime.now());
        return ResponseEntity.ok(repo.save(target));
    }

    @DeleteMapping
    public ResponseEntity<?> delete(@RequestParam int userId, @RequestParam int lessonId) {
        repo.findByUserIdAndLessonId(userId, lessonId).ifPresent(repo::delete);
        return ResponseEntity.ok(Map.of("success", true));
    }
}
