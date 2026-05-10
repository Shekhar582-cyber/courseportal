package com.example.courseportal.controller;

import com.example.courseportal.entity.Enrollment;
import com.example.courseportal.entity.LessonProgress;
import com.example.courseportal.entity.Notification;
import com.example.courseportal.repository.EnrollmentRepository;
import com.example.courseportal.repository.LessonProgressRepository;
import com.example.courseportal.repository.LessonRepository;
import com.example.courseportal.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@CrossOrigin
@RestController
@RequestMapping("/progress")
public class ProgressController {
    @Autowired private LessonProgressRepository progressRepo;
    @Autowired private LessonRepository lessonRepo;
    @Autowired private EnrollmentRepository enrollmentRepo;
    @Autowired private NotificationRepository notificationRepo;

    @GetMapping("/course")
    public ResponseEntity<?> getCourseProgress(@RequestParam int userId, @RequestParam int courseId) {
        return ResponseEntity.ok(summary(userId, courseId));
    }

    @PostMapping("/lesson")
    public ResponseEntity<?> markLesson(@RequestBody Map<String, Object> body) {
        int userId = ((Number) body.get("userId")).intValue();
        int courseId = ((Number) body.get("courseId")).intValue();
        int lessonId = ((Number) body.get("lessonId")).intValue();
        boolean completed = body.get("completed") == null || Boolean.parseBoolean(String.valueOf(body.get("completed")));

        LessonProgress lp = progressRepo.findByUserIdAndLessonId(userId, lessonId).orElseGet(LessonProgress::new);
        lp.setUserId(userId);
        lp.setCourseId(courseId);
        lp.setLessonId(lessonId);
        lp.setCompleted(completed);
        lp.setCompletedAt(completed ? LocalDateTime.now() : null);
        progressRepo.save(lp);

        Map<String, Object> summary = summary(userId, courseId);
        int pct = (int) summary.get("progress");
        enrollmentRepo.findByUserIdAndCourseId(userId, courseId).ifPresent(e -> {
            boolean wasCompleted = e.isCompleted();
            e.setProgress(pct);
            e.setCompleted(pct >= 100);
            enrollmentRepo.save(e);
            if (!wasCompleted && e.isCompleted()) notifyUser(userId, "certificate", "Certificate unlocked", "You completed the course. Your certificate is ready.");
        });
        return ResponseEntity.ok(summary);
    }

    private Map<String, Object> summary(int userId, int courseId) {
        long total = lessonRepo.countByCourseId(courseId);
        long done = progressRepo.countByUserIdAndCourseIdAndCompleted(userId, courseId, true);
        int pct = total == 0 ? 0 : (int) Math.round(done * 100.0 / total);
        List<Integer> completedLessonIds = progressRepo.findByUserIdAndCourseId(userId, courseId).stream()
                .filter(LessonProgress::isCompleted)
                .map(LessonProgress::getLessonId)
                .toList();
        Map<String, Object> result = new HashMap<>();
        result.put("totalLessons", total);
        result.put("completedLessons", done);
        result.put("completedLessonIds", completedLessonIds);
        result.put("progress", pct);
        return result;
    }

    private void notifyUser(int userId, String type, String title, String message) {
        Notification n = new Notification();
        n.setUserId(userId);
        n.setType(type);
        n.setTitle(title);
        n.setMessage(message);
        n.setCreatedAt(LocalDateTime.now());
        notificationRepo.save(n);
    }
}
