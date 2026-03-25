package com.example.courseportal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.courseportal.entity.User;

public interface UserRepository extends JpaRepository<User, Integer> {

    User findByEmailAndPassword(String email, String password);
}