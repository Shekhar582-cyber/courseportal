package com.example.courseportal.repository;

import com.example.courseportal.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    List<Notification> findByUserIdOrTargetRoleOrderByCreatedAtDesc(Integer userId, String targetRole);
}
