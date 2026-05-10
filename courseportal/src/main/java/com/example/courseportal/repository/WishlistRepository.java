package com.example.courseportal.repository;

import com.example.courseportal.entity.WishlistItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WishlistRepository extends JpaRepository<WishlistItem, Integer> {
    List<WishlistItem> findByUserId(int userId);
    Optional<WishlistItem> findByUserIdAndCourseId(int userId, int courseId);
    void deleteByUserIdAndCourseId(int userId, int courseId);
    void deleteByCourseId(int courseId);
}
