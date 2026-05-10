package com.example.courseportal.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "courses")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String title;
    private String category;   // web, data, ml, cloud, security, mobile
    private String level;      // Beginner, Intermediate, Advanced, All Levels

    @Column(columnDefinition = "TEXT")
    private String description;

    private String duration;   // e.g. "14 weeks"
    private int lessons;
    private int students;
    private double rating;
    private String price;      // "Free" or "$XX"
    private boolean premium;
    private double priceAmount;
    private String instructor;
    private Integer instructorUserId;

    @Column(columnDefinition = "TEXT")
    private String syllabus;

    private String thumbEmoji; // e.g. "🌐"

    @Column(length = 500)
    private String thumbGradient; // CSS gradient

    private String badge;      // badge-blue, badge-green, etc.
    private String link;       // enroll page link
    private Boolean published = true;

    public Course() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }
    public int getLessons() { return lessons; }
    public void setLessons(int lessons) { this.lessons = lessons; }
    public int getStudents() { return students; }
    public void setStudents(int students) { this.students = students; }
    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }
    public String getPrice() { return price; }
    public void setPrice(String price) { this.price = price; }
    public boolean isPremium() { return premium; }
    public void setPremium(boolean premium) { this.premium = premium; }
    public double getPriceAmount() { return priceAmount; }
    public void setPriceAmount(double priceAmount) { this.priceAmount = priceAmount; }
    public String getInstructor() { return instructor; }
    public void setInstructor(String instructor) { this.instructor = instructor; }
    public Integer getInstructorUserId() { return instructorUserId; }
    public void setInstructorUserId(Integer instructorUserId) { this.instructorUserId = instructorUserId; }
    public String getSyllabus() { return syllabus; }
    public void setSyllabus(String syllabus) { this.syllabus = syllabus; }
    public String getThumbEmoji() { return thumbEmoji; }
    public void setThumbEmoji(String thumbEmoji) { this.thumbEmoji = thumbEmoji; }
    public String getThumbGradient() { return thumbGradient; }
    public void setThumbGradient(String thumbGradient) { this.thumbGradient = thumbGradient; }
    public String getBadge() { return badge; }
    public void setBadge(String badge) { this.badge = badge; }
    public String getLink() { return link; }
    public void setLink(String link) { this.link = link; }
    public boolean isPublished() { return published == null || published; }
    public void setPublished(boolean published) { this.published = published; }
}
