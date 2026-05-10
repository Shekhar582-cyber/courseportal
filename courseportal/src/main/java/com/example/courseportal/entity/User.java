package com.example.courseportal.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User {

    @Id
    private int id;
    private String name;
    private String email;
    private String password;

    @Column(length = 700)
    private String photoUrl;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column(length = 700)
    private String skills;

    @Column(length = 20)
    private String theme = "dark"; // dark or light

    // Role: STUDENT (default) or ADMIN
    @Column(nullable = false, columnDefinition = "VARCHAR(20) DEFAULT 'STUDENT'")
    private String role = "STUDENT";

    public User() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public String getSkills() { return skills; }
    public void setSkills(String skills) { this.skills = skills; }

    public String getTheme() { return theme != null ? theme : "dark"; }
    public void setTheme(String theme) { this.theme = theme; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = (role != null ? role.toUpperCase() : "STUDENT"); }
}
