package com.example.courseportal.repository;

import com.example.courseportal.entity.QuizQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuizQuestionRepository extends JpaRepository<QuizQuestion, Integer> {
    List<QuizQuestion> findByCourseIdOrderByIdAsc(int courseId);
    List<QuizQuestion> findByLessonIdOrderByIdAsc(int lessonId);
    void deleteByCourseId(int courseId);
}
