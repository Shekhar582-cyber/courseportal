package com.example.courseportal.controller;

import com.example.courseportal.entity.Course;
import com.example.courseportal.entity.Enrollment;
import com.example.courseportal.repository.CourseRepository;
import com.example.courseportal.repository.EnrollmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
@RequestMapping("/recommendations")
public class RecommendationController {
    @Autowired private CourseRepository courseRepo;
    @Autowired private EnrollmentRepository enrollmentRepo;

    @GetMapping("/user/{userId}")
    public List<Course> recommendations(@PathVariable int userId) {
        List<Enrollment> enrollments = enrollmentRepo.findByUserId(userId);
        Set<Integer> enrolledIds = enrollments.stream().map(Enrollment::getCourseId).collect(Collectors.toSet());
        List<Course> courses = courseRepo.findAll();
        Set<String> categories = courses.stream()
                .filter(c -> enrolledIds.contains(c.getId()))
                .map(Course::getCategory)
                .collect(Collectors.toSet());
        List<Course> primary = courses.stream()
                .filter(Course::isPublished)
                .filter(c -> !enrolledIds.contains(c.getId()))
                .filter(c -> categories.isEmpty() || categories.contains(c.getCategory()))
                .sorted(Comparator.comparingDouble(Course::getRating).reversed())
                .limit(4)
                .toList();
        if (primary.size() >= 4) return primary;
        List<Course> fallback = courses.stream()
                .filter(Course::isPublished)
                .filter(c -> !enrolledIds.contains(c.getId()))
                .filter(c -> !primary.contains(c))
                .sorted(Comparator.comparingDouble(Course::getRating).reversed())
                .limit(4 - primary.size())
                .toList();
        List<Course> result = new ArrayList<>(primary);
        result.addAll(fallback);
        return result;
    }
}
