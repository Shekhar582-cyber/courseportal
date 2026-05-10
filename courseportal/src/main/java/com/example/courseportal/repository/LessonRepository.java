package com.example.courseportal.repository;

import com.example.courseportal.entity.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LessonRepository extends JpaRepository<Lesson, Integer> {
    List<Lesson> findByCourseIdOrderBySortOrderAscIdAsc(int courseId);
    long countByCourseId(int courseId);
    void deleteByCourseId(int courseId);
}
