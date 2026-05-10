package com.example.courseportal.repository;

import com.example.courseportal.entity.UserStreak;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface UserStreakRepository extends JpaRepository<UserStreak, Integer> {
    @Query("SELECT s FROM UserStreak s ORDER BY s.currentStreak DESC")
    List<UserStreak> findTopByCurrentStreak();
}
