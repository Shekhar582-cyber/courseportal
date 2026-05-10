package com.example.courseportal.controller;

import com.example.courseportal.repository.EnrollmentRepository;
import com.example.courseportal.repository.QuizAttemptRepository;
import com.example.courseportal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
@RequestMapping("/leaderboard")
public class LeaderboardController {

    @Autowired private EnrollmentRepository enrollmentRepo;
    @Autowired private UserRepository userRepo;
    @Autowired private QuizAttemptRepository quizRepo;

    @GetMapping
    public ResponseEntity<?> leaderboard() {
        // Group completed enrollments by userId
        Map<Integer, Long> completedByUser = enrollmentRepo.findAll().stream()
                .filter(e -> e.isCompleted())
                .collect(Collectors.groupingBy(
                        com.example.courseportal.entity.Enrollment::getUserId,
                        Collectors.counting()));

        // Build leaderboard entries
        List<Map<String, Object>> board = completedByUser.entrySet().stream()
                .sorted(Map.Entry.<Integer, Long>comparingByValue().reversed())
                .limit(10)
                .map(entry -> {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("userId", entry.getKey());
                    row.put("coursesCompleted", entry.getValue());
                    userRepo.findById(entry.getKey()).ifPresent(u -> {
                        row.put("name", u.getName());
                        row.put("photoUrl", u.getPhotoUrl());
                    });
                    return row;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(board);
    }
}
