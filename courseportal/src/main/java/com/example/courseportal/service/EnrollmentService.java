package com.example.courseportal.service;

import com.example.courseportal.entity.Enrollment;
import com.example.courseportal.repository.CourseRepository;
import com.example.courseportal.repository.EnrollmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class EnrollmentService {

    @Autowired
    private EnrollmentRepository enrollmentRepo;

    @Autowired
    private CourseRepository courseRepo;

    /** Enroll a user in a course (prevents duplicates). */
    public Map<String, Object> enroll(int userId, int courseId) {
        Map<String, Object> result = new HashMap<>();
        if (enrollmentRepo.findByUserIdAndCourseId(userId, courseId).isPresent()) {
            result.put("success", false);
            result.put("message", "Already enrolled in this course");
            return result;
        }
        if (!courseRepo.existsById(courseId)) {
            result.put("success", false);
            result.put("message", "Course not found");
            return result;
        }
        Enrollment e = new Enrollment();
        e.setUserId(userId);
        e.setCourseId(courseId);
        e.setEnrolledAt(LocalDateTime.now());
        e.setProgress(0);
        e.setCompleted(false);
        result.put("success", true);
        result.put("enrollment", enrollmentRepo.save(e));
        return result;
    }

    /** Get enriched enrollment list (enrollment + course) for a user. */
    public List<Map<String, Object>> getEnrollmentsWithCourses(int userId) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Enrollment e : enrollmentRepo.findByUserId(userId)) {
            Map<String, Object> item = new HashMap<>();
            item.put("enrollmentId", e.getId());
            item.put("userId", e.getUserId());
            item.put("courseId", e.getCourseId());
            item.put("enrolledAt", e.getEnrolledAt());
            item.put("progress", e.getProgress());
            item.put("completed", e.isCompleted());
            courseRepo.findById(e.getCourseId()).ifPresent(c -> item.put("course", c));
            result.add(item);
        }
        return result;
    }

    /** Update progress (0–100). Auto-completes at 100. */
    public Map<String, Object> updateProgress(int enrollmentId, int progress) {
        Map<String, Object> result = new HashMap<>();
        Optional<Enrollment> opt = enrollmentRepo.findById(enrollmentId);
        if (opt.isEmpty()) {
            result.put("success", false);
            result.put("message", "Enrollment not found");
            return result;
        }
        Enrollment e = opt.get();
        e.setProgress(Math.min(100, Math.max(0, progress)));
        e.setCompleted(e.getProgress() >= 100);
        result.put("success", true);
        result.put("enrollment", enrollmentRepo.save(e));
        return result;
    }

    public boolean isEnrolled(int userId, int courseId) {
        return enrollmentRepo.findByUserIdAndCourseId(userId, courseId).isPresent();
    }

    public long countEnrolled(int userId)   { return enrollmentRepo.countByUserId(userId); }
    public long countCompleted(int userId)  { return enrollmentRepo.countByUserIdAndCompleted(userId, true); }
}
