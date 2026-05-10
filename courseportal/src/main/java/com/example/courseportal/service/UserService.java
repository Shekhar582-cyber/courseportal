package com.example.courseportal.service;

import com.example.courseportal.entity.User;
import com.example.courseportal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class UserService {
    @Autowired
    private UserRepository repo;
    //get all users
    public List<User> getAllUsers(){
        return repo.findAll();
    }
    //save users
    public User saveUser(User user){
        return repo.save(user);
    }
    //login logic
    public User login(String email,String password){
        if (email == null || password == null) {
            return null;
        }
        return repo.findFirstByEmailAndPasswordOrderByIdAsc(email.trim(), password);
    }

    public User findByEmail(String email) {
        if (email == null || email.isBlank()) {
            return null;
        }
        return repo.findFirstByEmailIgnoreCaseOrderByIdAsc(email.trim());
    }

    public User findOrCreateGoogleUser(String name, String email) {
        String cleanEmail = email != null ? email.trim() : "";
        if (cleanEmail.isBlank()) {
            return null;
        }

        User existing = findByEmail(cleanEmail);
        if (existing != null) {
            if ((existing.getName() == null || existing.getName().isBlank()) && name != null && !name.isBlank()) {
                existing.setName(name.trim());
                return repo.save(existing);
            }
            return existing;
        }

        User user = new User();
        user.setId(generateUserId());
        user.setName(name != null && !name.isBlank() ? name.trim() : cleanEmail.split("@")[0]);
        user.setEmail(cleanEmail);
        user.setPassword("GOOGLE_AUTH");
        user.setRole("STUDENT");
        return repo.save(user);
    }

    public void deleteUser(int id){
        repo.deleteById(id);
    }
    public User updateUser(User user) {
        return repo.findById(user.getId()).map(existing -> {
            if (user.getName() != null && !user.getName().isBlank()) existing.setName(user.getName());
            if (user.getEmail() != null && !user.getEmail().isBlank()) existing.setEmail(user.getEmail());
            if (user.getPassword() != null && !user.getPassword().isBlank()) existing.setPassword(user.getPassword());
            if (user.getRole() != null && !user.getRole().isBlank()) existing.setRole(user.getRole());
            existing.setPhotoUrl(user.getPhotoUrl());
            existing.setBio(user.getBio());
            existing.setSkills(user.getSkills());
            return repo.save(existing);
        }).orElseGet(() -> repo.save(user));
    }

    private int generateUserId() {
        int id;
        do {
            id = ThreadLocalRandom.current().nextInt(100000, 999999);
        } while (repo.existsById(id));
        return id;
    }

}
