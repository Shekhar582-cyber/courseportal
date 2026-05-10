package com.example.courseportal.repository;

import com.example.courseportal.entity.LessonBookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface LessonBookmarkRepository extends JpaRepository<LessonBookmark, Integer> {
    List<LessonBookmark> findByUserIdOrderByCreatedAtDesc(int userId);
    Optional<LessonBookmark> findByUserIdAndLessonId(int userId, int lessonId);
    void deleteByUserIdAndLessonId(int userId, int lessonId);
}
