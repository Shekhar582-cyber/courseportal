package com.example.courseportal.repository;

import com.example.courseportal.entity.LessonNote;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface LessonNoteRepository extends JpaRepository<LessonNote, Integer> {
    Optional<LessonNote> findByUserIdAndLessonId(int userId, int lessonId);
    List<LessonNote> findByUserIdAndCourseId(int userId, int courseId);
    List<LessonNote> findByUserId(int userId);
}
