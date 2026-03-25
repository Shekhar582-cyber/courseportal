package com.example.courseportal.controller;

import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.courseportal.entity.User;
import com.example.courseportal.repository.UserRepository;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "http://localhost:63342")
public class UserController {
    @PostMapping("/add")
    public User addUser(@RequestBody User user) {
        return repo.save(user);
    }
    @PostMapping("/login")
    public User login(@RequestBody User user) {
        return repo.findByEmailAndPassword(user.getEmail(), user.getPassword());
    }
    @Autowired
    private UserRepository repo;

    @GetMapping("/test")
    public String test() {
        return "API Working ✅";
    }

    @GetMapping("/all")
    public List<User> getAllUsers() {
        return repo.findAll();
    }
}