package com.example.courseportal.controller;

import com.example.courseportal.entity.UserStreak;
import com.example.courseportal.repository.UserRepository;
import com.example.courseportal.repository.UserStreakRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
@RequestMapping("/streaks")
public class StreakController {

    @Autowired private UserStreakRepository streakRepo;
    @Autowired private UserRepository userRepo;

    @GetMapping("/{userId}")
    public ResponseEntity<?> get(@PathVariable int userId) {
        return ResponseEntity.ok(streakRepo.findById(userId).orElseGet(() -> {
            UserStreak s = new UserStreak();
            s.setUserId(userId);
            return s;
        }));
    }

    @PostMapping("/activity/{userId}")
    public ResponseEntity<?> recordActivity(@PathVariable int userId) {
        UserStreak streak = streakRepo.findById(userId).orElseGet(() -> {
            UserStreak s = new UserStreak();
            s.setUserId(userId);
            s.setCurrentStreak(0);
            s.setLongestStreak(0);
            return s;
        });

        LocalDate today = LocalDate.now();
        LocalDate last = streak.getLastActivityDate();

        if (last == null || last.isBefore(today.minusDays(1))) {
            // Streak broken or first activity
            streak.setCurrentStreak(1);
        } else if (last.equals(today.minusDays(1))) {
            // Consecutive day
            streak.setCurrentStreak(streak.getCurrentStreak() + 1);
        }
        // Same day — no change to streak count

        if (streak.getCurrentStreak() > streak.getLongestStreak()) {
            streak.setLongestStreak(streak.getCurrentStreak());
        }
        streak.setLastActivityDate(today);
        return ResponseEntity.ok(streakRepo.save(streak));
    }

    @GetMapping("/leaderboard")
    public ResponseEntity<?> leaderboard() {
        List<UserStreak> top = streakRepo.findTopByCurrentStreak()
                .stream().limit(10).collect(Collectors.toList());

        List<Map<String, Object>> result = top.stream().map(s -> {
            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("userId", s.getUserId());
            entry.put("currentStreak", s.getCurrentStreak());
            entry.put("longestStreak", s.getLongestStreak());
            userRepo.findById(s.getUserId()).ifPresent(u -> {
                entry.put("name", u.getName());
                entry.put("photoUrl", u.getPhotoUrl());
            });
            return entry;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }
}
