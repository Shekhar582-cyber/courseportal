package com.example.courseportal.controller;

import com.example.courseportal.entity.Course;
import com.example.courseportal.entity.Enrollment;
import com.example.courseportal.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
@RequestMapping("/analytics")
public class AnalyticsController {
    @Autowired private CourseRepository courseRepo;
    @Autowired private EnrollmentRepository enrollmentRepo;
    @Autowired private UserRepository userRepo;
    @Autowired private ReviewRepository reviewRepo;
    @Autowired private PaymentRepository paymentRepo;

    @GetMapping("/admin")
    public ResponseEntity<?> admin(@RequestHeader(value = "X-User-Role", required = false) String role) {
        if (!"ADMIN".equalsIgnoreCase(role)) {
            return ResponseEntity.status(403).body(Map.of("success", false, "message", "Admins only."));
        }
        List<Course> courses = courseRepo.findAll();
        List<Enrollment> enrollments = enrollmentRepo.findAll();
        Map<Integer, Long> counts = enrollments.stream().collect(Collectors.groupingBy(Enrollment::getCourseId, Collectors.counting()));
        List<Map<String, Object>> popular = courses.stream()
                .map(c -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("courseId", c.getId());
                    item.put("title", c.getTitle());
                    item.put("enrollments", counts.getOrDefault(c.getId(), 0L));
                    item.put("rating", c.getRating());
                    return item;
                })
                .sorted((a, b) -> Long.compare((Long) b.get("enrollments"), (Long) a.get("enrollments")))
                .toList();
        long completed = enrollments.stream().filter(Enrollment::isCompleted).count();
        double completionRate = enrollments.isEmpty() ? 0 : Math.round(completed * 1000.0 / enrollments.size()) / 10.0;
        return ResponseEntity.ok(Map.of(
                "totalStudents", userRepo.count(),
                "totalCourses", courseRepo.count(),
                "totalEnrollments", enrollments.size(),
                "completedEnrollments", completed,
                "completionRate", completionRate,
                "totalReviews", reviewRepo.count(),
                "paidPayments", paymentRepo.countByStatus("PAID"),
                "popularCourses", popular));
    }
}
