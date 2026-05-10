package com.example.courseportal.controller;

import com.example.courseportal.repository.CourseRepository;
import com.example.courseportal.repository.EnrollmentRepository;
import com.example.courseportal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/certificates")
public class CertificateController {

    @Autowired
    private EnrollmentRepository enrollmentRepo;

    @Autowired
    private CourseRepository courseRepo;

    @Autowired
    private UserRepository userRepo;

    @GetMapping("/user/{userId}/course/{courseId}")
    public ResponseEntity<?> certificate(@PathVariable int userId, @PathVariable int courseId) {
        boolean completed = enrollmentRepo.findByUserIdAndCourseId(userId, courseId)
                .map(e -> e.isCompleted() || e.getProgress() >= 100)
                .orElse(false);
        if (!completed) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Course is not complete yet."));
        }
        Map<String, Object> data = new HashMap<>();
        data.put("success", true);
        data.put("issuedAt", LocalDate.now().toString());
        data.put("certificateId", "CP-" + userId + "-" + courseId);
        userRepo.findById(userId).ifPresent(u -> data.put("studentName", u.getName()));
        courseRepo.findById(courseId).ifPresent(c -> data.put("courseTitle", c.getTitle()));
        return ResponseEntity.ok(data);
    }
}
