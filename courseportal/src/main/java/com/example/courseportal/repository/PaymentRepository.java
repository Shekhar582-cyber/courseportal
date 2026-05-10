package com.example.courseportal.repository;

import com.example.courseportal.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    List<Payment> findByUserIdOrderByCreatedAtDesc(int userId);
    Optional<Payment> findFirstByUserIdAndCourseIdAndStatusOrderByCreatedAtDesc(int userId, int courseId, String status);
    long countByStatus(String status);
}
