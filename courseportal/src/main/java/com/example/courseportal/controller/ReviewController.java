package com.example.courseportal.controller;

import com.example.courseportal.entity.Review;
import com.example.courseportal.repository.CourseRepository;
import com.example.courseportal.repository.EnrollmentRepository;
import com.example.courseportal.repository.ReviewRepository;
import com.example.courseportal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@CrossOrigin
@RestController
@RequestMapping("/reviews")
public class ReviewController {

    @Autowired private ReviewRepository reviewRepo;
    @Autowired private EnrollmentRepository enrollmentRepo;
    @Autowired private CourseRepository courseRepo;
    @Autowired private UserRepository userRepo;

    @GetMapping("/course/{courseId}")
    public ResponseEntity<?> getByCourse(@PathVariable int courseId) {
        return ResponseEntity.ok(reviewRepo.findByCourseIdOrderByCreatedAtDesc(courseId));
    }

    @PostMapping("/save")
    public ResponseEntity<?> save(@RequestBody Review review) {
        if (!courseRepo.existsById(review.getCourseId())) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Course not found"));
        }
        boolean completed = enrollmentRepo.findByUserIdAndCourseId(review.getUserId(), review.getCourseId())
                .map(e -> e.isCompleted() || e.getProgress() >= 100)
                .orElse(false);
        if (!completed) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Complete the course before reviewing."));
        }
        int rating = Math.max(1, Math.min(5, review.getRating()));
        Optional<Review> existing = reviewRepo.findByUserIdAndCourseId(review.getUserId(), review.getCourseId());
        Review target = existing.orElseGet(Review::new);
        target.setUserId(review.getUserId());
        target.setCourseId(review.getCourseId());
        target.setRating(rating);
        target.setComment(review.getComment());
        target.setCreatedAt(LocalDateTime.now());
        // Store user name for display
        userRepo.findById(review.getUserId()).ifPresent(u -> target.setUserName(u.getName()));
        return ResponseEntity.ok(reviewRepo.save(target));
    }
}
