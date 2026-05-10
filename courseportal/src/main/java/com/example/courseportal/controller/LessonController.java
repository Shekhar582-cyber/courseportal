package com.example.courseportal.controller;

import com.example.courseportal.entity.Lesson;
import com.example.courseportal.entity.Notification;
import com.example.courseportal.repository.CourseRepository;
import com.example.courseportal.repository.LessonRepository;
import com.example.courseportal.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.time.LocalDateTime;

@CrossOrigin
@RestController
@RequestMapping("/lessons")
public class LessonController {

    @Autowired
    private LessonRepository lessonRepo;

    @Autowired
    private CourseRepository courseRepo;

    @Autowired
    private NotificationRepository notificationRepo;

    @GetMapping("/course/{courseId}")
    public ResponseEntity<?> getByCourse(@PathVariable int courseId) {
        if (!courseRepo.existsById(courseId)) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(lessonRepo.findByCourseIdOrderBySortOrderAscIdAsc(courseId));
    }

    @PostMapping("/save")
    public ResponseEntity<?> save(
            @RequestBody Lesson lesson,
            @RequestHeader(value = "X-User-Role", required = false) String role) {
        if (!canManage(role)) return forbidden();
        if (!courseRepo.existsById(lesson.getCourseId())) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Course not found"));
        }
        if (lesson.getType() == null || lesson.getType().isBlank()) lesson.setType("LINK");
        Lesson saved = lessonRepo.save(lesson);
        Notification n = new Notification();
        n.setTargetRole("STUDENT");
        n.setType("lesson");
        n.setTitle("New lesson added");
        n.setMessage("A lesson was added: " + saved.getTitle());
        n.setCreatedAt(LocalDateTime.now());
        notificationRepo.save(n);
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(
            @PathVariable int id,
            @RequestHeader(value = "X-User-Role", required = false) String role) {
        if (!canManage(role)) return forbidden();
        lessonRepo.deleteById(id);
        return ResponseEntity.ok(Map.of("success", true));
    }

    private boolean canManage(String role) {
        return "ADMIN".equalsIgnoreCase(role) || "INSTRUCTOR".equalsIgnoreCase(role);
    }

    private ResponseEntity<?> forbidden() {
        return ResponseEntity.status(403).body(Map.of("success", false, "message", "Access denied. Admins only."));
    }
}
