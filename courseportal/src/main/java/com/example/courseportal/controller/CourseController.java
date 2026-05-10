package com.example.courseportal.controller;

import com.example.courseportal.entity.Course;
import com.example.courseportal.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@CrossOrigin
@RestController
@RequestMapping("/courses")
public class CourseController {

    @Autowired
    private CourseService service;

    // ── PUBLIC ────────────────────────────────────────────────────
    @GetMapping("/all")
    public List<Course> getAllCourses() {
        return service.getAllCourses();
    }

    @GetMapping("/instructor/{userId}")
    public List<Course> getInstructorCourses(
            @PathVariable int userId,
            @RequestHeader(value = "X-User-Id", required = false) Integer currentUserId,
            @RequestHeader(value = "X-User-Role", required = false) String role) {
        if ("ADMIN".equalsIgnoreCase(role)) {
            return service.getCoursesForInstructor(userId);
        }
        if (!"INSTRUCTOR".equalsIgnoreCase(role) || currentUserId == null || currentUserId != userId) {
            return Collections.emptyList();
        }
        return service.getCoursesForInstructor(userId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCourse(@PathVariable int id) {
        return service.getCourseById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public List<Course> search(
            @RequestParam(defaultValue = "") String q,
            @RequestParam(defaultValue = "all") String cat,
            @RequestParam(defaultValue = "all") String level) {
        return service.searchCourses(q, cat, level);
    }

    // ── ADMIN ONLY ────────────────────────────────────────────────
    @PostMapping("/add")
    public ResponseEntity<?> addCourse(
            @RequestBody Course course,
            @RequestHeader(value = "X-User-Role", required = false) String role) {
        if (!"ADMIN".equalsIgnoreCase(role)) return forbidden();
        return ResponseEntity.ok(service.saveCourse(course));
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateCourse(
            @RequestBody Course course,
            @RequestHeader(value = "X-User-Id", required = false) Integer currentUserId,
            @RequestHeader(value = "X-User-Role", required = false) String role) {
        if (!canManageCourse(role, currentUserId, course)) return forbidden();
        return ResponseEntity.ok(service.saveCourse(course));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteCourse(
            @PathVariable int id,
            @RequestHeader(value = "X-User-Role", required = false) String role) {
        if (!"ADMIN".equalsIgnoreCase(role)) return forbidden();
        service.deleteCourse(id);
        return ResponseEntity.ok(Map.of("success", true));
    }

    @PutMapping("/{id}/publish")
    public ResponseEntity<?> togglePublish(
            @PathVariable int id,
            @RequestBody Map<String, Boolean> body,
            @RequestHeader(value = "X-User-Id", required = false) Integer currentUserId,
            @RequestHeader(value = "X-User-Role", required = false) String role) {
        return service.getCourseById(id).map(course -> {
            if (!canManageCourse(role, currentUserId, course)) return forbidden();
            course.setPublished(Boolean.TRUE.equals(body.get("published")));
            return ResponseEntity.ok(service.saveCourse(course));
        }).orElse(ResponseEntity.notFound().build());
    }

    private ResponseEntity<?> forbidden() {
        return ResponseEntity.status(403)
                .body(Map.of("success", false, "message", "Access denied. Admins only."));
    }

    private boolean canManageCourse(String role, Integer currentUserId, Course course) {
        if ("ADMIN".equalsIgnoreCase(role)) return true;
        return "INSTRUCTOR".equalsIgnoreCase(role)
                && course != null
                && course.getInstructorUserId() != null
                && currentUserId != null
                && course.getInstructorUserId().intValue() == currentUserId.intValue();
    }
}
