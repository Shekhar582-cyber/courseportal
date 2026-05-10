package com.example.courseportal.repository;

import com.example.courseportal.entity.QuizAttempt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Integer> {
    List<QuizAttempt> findByUserIdOrderBySubmittedAtDesc(int userId);
    List<QuizAttempt> findByUserIdAndCourseIdOrderBySubmittedAtDesc(int userId, int courseId);
    void deleteByCourseId(int courseId);
}
