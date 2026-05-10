package com.example.courseportal.repository;

import com.example.courseportal.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Integer> {
    List<Review> findByCourseIdOrderByCreatedAtDesc(int courseId);
    Optional<Review> findByUserIdAndCourseId(int userId, int courseId);
    void deleteByCourseId(int courseId);
}
