package com.example.courseportal.service;

import com.example.courseportal.entity.User;
import com.example.courseportal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
        return repo.findByEmailAndPassword(email,password);
    }
    public void deleteUser(int id){
        repo.deleteById(id);
    }
    public User updateUser(User user) {
        return repo.save(user);
    }

}
