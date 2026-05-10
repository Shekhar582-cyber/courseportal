package com.example.courseportal.repository;

import com.example.courseportal.entity.Discussion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DiscussionRepository extends JpaRepository<Discussion, Integer> {
    List<Discussion> findByLessonIdOrderByCreatedAtAsc(int lessonId);
    List<Discussion> findByCourseIdOrderByCreatedAtDesc(int courseId);
}
