package com.example.courseportal.controller;

import com.example.courseportal.service.EnrollmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/enrollments")
public class EnrollmentController {

    @Autowired
    private EnrollmentService service;

    /** Enroll: POST /enrollments/enroll  body: { userId, courseId } */
    @PostMapping("/enroll")
    public ResponseEntity<Map<String, Object>> enroll(@RequestBody Map<String, Integer> body) {
        int userId   = body.get("userId");
        int courseId = body.get("courseId");
        Map<String, Object> result = service.enroll(userId, courseId);
        return (boolean) result.get("success")
                ? ResponseEntity.ok(result)
                : ResponseEntity.badRequest().body(result);
    }

    /** Get all enrollments + course info for a user: GET /enrollments/user/{userId} */
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserEnrollments(@PathVariable int userId) {
        return ResponseEntity.ok(service.getEnrollmentsWithCourses(userId));
    }

    /** Update progress: PUT /enrollments/progress  body: { enrollmentId, progress } */
    @PutMapping("/progress")
    public ResponseEntity<Map<String, Object>> updateProgress(@RequestBody Map<String, Integer> body) {
        int enrollmentId = body.get("enrollmentId");
        int progress     = body.get("progress");
        Map<String, Object> result = service.updateProgress(enrollmentId, progress);
        return (boolean) result.get("success")
                ? ResponseEntity.ok(result)
                : ResponseEntity.badRequest().body(result);
    }

    /** Check enrollment: GET /enrollments/check?userId=&courseId= */
    @GetMapping("/check")
    public ResponseEntity<?> check(@RequestParam int userId, @RequestParam int courseId) {
        return ResponseEntity.ok(Map.of(
                "enrolled", service.isEnrolled(userId, courseId),
                "userId", userId,
                "courseId", courseId));
    }

    /** Stats: GET /enrollments/stats/{userId} */
    @GetMapping("/stats/{userId}")
    public ResponseEntity<?> stats(@PathVariable int userId) {
        return ResponseEntity.ok(Map.of(
                "totalEnrolled", service.countEnrolled(userId),
                "completed",     service.countCompleted(userId)));
    }

    /** Bulk enroll: POST /enrollments/bulk  body: { userIds: [1,2,3], courseId: 5 } */
    @PostMapping("/bulk")
    public ResponseEntity<?> bulkEnroll(
            @RequestBody Map<String, Object> body,
            @RequestHeader(value = "X-User-Role", required = false) String role) {
        if (!"ADMIN".equalsIgnoreCase(role)) {
            return ResponseEntity.status(403).body(Map.of("success", false, "message", "Admins only"));
        }
        @SuppressWarnings("unchecked")
        java.util.List<Integer> userIds = (java.util.List<Integer>) body.get("userIds");
        int courseId = ((Number) body.get("courseId")).intValue();
        int enrolled = 0, skipped = 0;
        for (int uid : userIds) {
            Map<String, Object> r = service.enroll(uid, courseId);
            if ((boolean) r.get("success")) enrolled++; else skipped++;
        }
        return ResponseEntity.ok(Map.of("success", true, "enrolled", enrolled, "skipped", skipped));
    }
}
