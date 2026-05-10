package com.example.courseportal.controller;

import com.example.courseportal.entity.Course;
import com.example.courseportal.entity.Payment;
import com.example.courseportal.repository.CourseRepository;
import com.example.courseportal.repository.PaymentRepository;
import com.example.courseportal.service.EnrollmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/payments")
public class PaymentController {
    @Autowired private PaymentRepository paymentRepo;
    @Autowired private CourseRepository courseRepo;
    @Autowired private EnrollmentService enrollmentService;

    @PostMapping("/checkout")
    public ResponseEntity<?> checkout(@RequestBody Map<String, Object> body) {
        int userId = ((Number) body.get("userId")).intValue();
        int courseId = ((Number) body.get("courseId")).intValue();
        String method = String.valueOf(body.getOrDefault("method", "demo"));
        Course course = courseRepo.findById(courseId).orElse(null);
        if (course == null) return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Course not found"));

        Payment payment = new Payment();
        payment.setUserId(userId);
        payment.setCourseId(courseId);
        payment.setAmount(course.isPremium() ? course.getPriceAmount() : 0);
        payment.setMethod(method);
        payment.setStatus("PAID");
        payment.setCreatedAt(LocalDateTime.now());
        Payment saved = paymentRepo.save(payment);
        enrollmentService.enroll(userId, courseId);
        return ResponseEntity.ok(Map.of("success", true, "payment", saved));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> userPayments(@PathVariable int userId) {
        return ResponseEntity.ok(paymentRepo.findByUserIdOrderByCreatedAtDesc(userId));
    }

    @GetMapping("/access")
    public ResponseEntity<?> access(@RequestParam int userId, @RequestParam int courseId) {
        return ResponseEntity.ok(Map.of(
                "paid", paymentRepo.findFirstByUserIdAndCourseIdAndStatusOrderByCreatedAtDesc(userId, courseId, "PAID").isPresent()));
    }

    @GetMapping("/revenue")
    public ResponseEntity<?> revenue(@RequestHeader(value = "X-User-Role", required = false) String role) {
        if (!"ADMIN".equalsIgnoreCase(role)) {
            return ResponseEntity.status(403).body(Map.of("success", false, "message", "Admins only"));
        }
        var payments = paymentRepo.findAll();
        double total = payments.stream().mapToDouble(p -> p.getAmount()).sum();

        // Revenue per course
        Map<Integer, Double> byCourse = new java.util.LinkedHashMap<>();
        payments.forEach(p -> byCourse.merge(p.getCourseId(), p.getAmount(), Double::sum));

        var topCourses = byCourse.entrySet().stream()
                .sorted(Map.Entry.<Integer, Double>comparingByValue().reversed())
                .limit(10)
                .map(e -> {
                    Map<String, Object> row = new java.util.LinkedHashMap<>();
                    row.put("courseId", e.getKey());
                    row.put("revenue", e.getValue());
                    courseRepo.findById(e.getKey()).ifPresent(c -> row.put("title", c.getTitle()));
                    return row;
                }).toList();

        return ResponseEntity.ok(Map.of(
                "totalRevenue", total,
                "totalPayments", payments.size(),
                "topCourses", topCourses
        ));
    }
}
