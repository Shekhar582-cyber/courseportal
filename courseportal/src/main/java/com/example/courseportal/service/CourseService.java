package com.example.courseportal.service;

import com.example.courseportal.entity.Course;
import com.example.courseportal.repository.EnrollmentRepository;
import com.example.courseportal.repository.LessonRepository;
import com.example.courseportal.repository.LessonProgressRepository;
import com.example.courseportal.repository.CourseRepository;
import com.example.courseportal.repository.QuizAttemptRepository;
import com.example.courseportal.repository.QuizQuestionRepository;
import com.example.courseportal.repository.ReviewRepository;
import com.example.courseportal.repository.WishlistRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CourseService {

    @Autowired
    private CourseRepository repo;

    @Autowired
    private LessonRepository lessonRepo;

    @Autowired
    private EnrollmentRepository enrollmentRepo;

    @Autowired
    private WishlistRepository wishlistRepo;

    @Autowired
    private ReviewRepository reviewRepo;

    @Autowired
    private LessonProgressRepository lessonProgressRepo;

    @Autowired
    private QuizQuestionRepository quizQuestionRepo;

    @Autowired
    private QuizAttemptRepository quizAttemptRepo;

    public List<Course> getAllCourses() { return repo.findAll(); }

    public List<Course> getCoursesForInstructor(int instructorUserId) {
        return repo.findByInstructorUserId(instructorUserId);
    }

    public Optional<Course> getCourseById(int id) { return repo.findById(id); }

    public Course saveCourse(Course course) { return repo.save(course); }

    @Transactional
    public void deleteCourse(int id) {
        lessonRepo.deleteByCourseId(id);
        wishlistRepo.deleteByCourseId(id);
        reviewRepo.deleteByCourseId(id);
        lessonProgressRepo.deleteByCourseId(id);
        quizQuestionRepo.deleteByCourseId(id);
        quizAttemptRepo.deleteByCourseId(id);
        enrollmentRepo.deleteByCourseId(id);
        repo.deleteById(id);
    }

    public List<Course> searchCourses(String q, String cat, String level) {
        String qParam = (q == null || q.isBlank()) ? null : q;
        return repo.search(qParam, cat, level);
    }
}
