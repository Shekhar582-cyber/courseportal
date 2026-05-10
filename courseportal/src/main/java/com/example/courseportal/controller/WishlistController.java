package com.example.courseportal.controller;

import com.example.courseportal.entity.WishlistItem;
import com.example.courseportal.repository.CourseRepository;
import com.example.courseportal.repository.WishlistRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@CrossOrigin
@RestController
@RequestMapping("/wishlist")
public class WishlistController {

    @Autowired
    private WishlistRepository wishlistRepo;

    @Autowired
    private CourseRepository courseRepo;

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserWishlist(@PathVariable int userId) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (WishlistItem item : wishlistRepo.findByUserId(userId)) {
            courseRepo.findById(item.getCourseId()).ifPresent(course -> {
                Map<String, Object> row = new HashMap<>();
                row.put("id", item.getId());
                row.put("userId", item.getUserId());
                row.put("courseId", item.getCourseId());
                row.put("savedAt", item.getSavedAt());
                row.put("course", course);
                result.add(row);
            });
        }
        return ResponseEntity.ok(result);
    }

    @PostMapping("/save")
    public ResponseEntity<?> save(@RequestBody Map<String, Integer> body) {
        int userId = body.get("userId");
        int courseId = body.get("courseId");
        if (!courseRepo.existsById(courseId)) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Course not found"));
        }
        Optional<WishlistItem> existing = wishlistRepo.findByUserIdAndCourseId(userId, courseId);
        if (existing.isPresent()) return ResponseEntity.ok(Map.of("success", true, "saved", true));
        WishlistItem item = new WishlistItem();
        item.setUserId(userId);
        item.setCourseId(courseId);
        item.setSavedAt(LocalDateTime.now());
        wishlistRepo.save(item);
        return ResponseEntity.ok(Map.of("success", true, "saved", true));
    }

    @Transactional
    @DeleteMapping("/remove")
    public ResponseEntity<?> remove(@RequestParam int userId, @RequestParam int courseId) {
        wishlistRepo.deleteByUserIdAndCourseId(userId, courseId);
        return ResponseEntity.ok(Map.of("success", true, "saved", false));
    }
}
