package com.example.courseportal.repository;

import com.example.courseportal.entity.LessonProgress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LessonProgressRepository extends JpaRepository<LessonProgress, Integer> {
    List<LessonProgress> findByUserIdAndCourseId(int userId, int courseId);
    Optional<LessonProgress> findByUserIdAndLessonId(int userId, int lessonId);
    long countByUserIdAndCourseIdAndCompleted(int userId, int courseId, boolean completed);
    void deleteByCourseId(int courseId);
}
