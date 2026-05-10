package com.example.courseportal.repository;

import com.example.courseportal.entity.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Integer> {

    List<Enrollment> findByUserId(int userId);
    List<Enrollment> findByCourseId(int courseId);
    Optional<Enrollment> findByUserIdAndCourseId(int userId, int courseId);
    void deleteByCourseId(int courseId);
    long countByUserId(int userId);
    long countByUserIdAndCompleted(int userId, boolean completed);
}
