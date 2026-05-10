package com.example.courseportal.controller;

import com.example.courseportal.entity.Notification;
import com.example.courseportal.entity.QuizAttempt;
import com.example.courseportal.entity.QuizQuestion;
import com.example.courseportal.repository.CourseRepository;
import com.example.courseportal.repository.LessonRepository;
import com.example.courseportal.repository.NotificationRepository;
import com.example.courseportal.repository.QuizAttemptRepository;
import com.example.courseportal.repository.QuizQuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@CrossOrigin
@RestController
@RequestMapping("/quizzes")
public class QuizController {
    @Autowired private QuizQuestionRepository questionRepo;
    @Autowired private QuizAttemptRepository attemptRepo;
    @Autowired private NotificationRepository notificationRepo;
    @Autowired private CourseRepository courseRepo;
    @Autowired private LessonRepository lessonRepo;

    @GetMapping("/course/{courseId}")
    public List<QuizQuestion> byCourse(@PathVariable int courseId) {
        return questionRepo.findByCourseIdOrderByIdAsc(courseId);
    }

    @GetMapping("/lesson/{lessonId}")
    public List<Map<String, Object>> byLesson(@PathVariable int lessonId) {
        return questionRepo.findByLessonIdOrderByIdAsc(lessonId).stream().map(this::publicQuestion).toList();
    }

    @PostMapping("/question")
    public ResponseEntity<?> saveQuestion(
            @RequestBody QuizQuestion question,
            @RequestHeader(value = "X-User-Role", required = false) String role) {
        if (!canManage(role)) return forbidden();
        if (question.getCorrectOption() == null || question.getCorrectOption().isBlank()) question.setCorrectOption("A");
        QuizQuestion saved = questionRepo.save(question);

        // Build notification message with course and lesson names
        String courseName = courseRepo.findById(question.getCourseId())
                .map(c -> c.getTitle()).orElse("a course");
        String lessonName = lessonRepo.findById(question.getLessonId())
                .map(l -> l.getTitle()).orElse("a lesson");

        Notification n = new Notification();
        n.setTargetRole("STUDENT");
        n.setType("quiz");
        n.setTitle("📝 New Quiz Available");
        n.setMessage("A new quiz has been added to \"" + lessonName + "\" in " + courseName + ". Test your knowledge now!");
        n.setCreatedAt(LocalDateTime.now());
        n.setReadStatus(false);
        notificationRepo.save(n);

        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/question/{id}")
    public ResponseEntity<?> deleteQuestion(
            @PathVariable int id,
            @RequestHeader(value = "X-User-Role", required = false) String role) {
        if (!canManage(role)) return forbidden();
        questionRepo.deleteById(id);
        return ResponseEntity.ok(Map.of("success", true));
    }

    @PostMapping("/submit")
    public ResponseEntity<?> submit(@RequestBody Map<String, Object> body) {
        int userId = ((Number) body.get("userId")).intValue();
        int courseId = ((Number) body.get("courseId")).intValue();
        int lessonId = ((Number) body.get("lessonId")).intValue();
        Map<?, ?> answers = (Map<?, ?>) body.getOrDefault("answers", Map.of());
        List<QuizQuestion> questions = questionRepo.findByLessonIdOrderByIdAsc(lessonId);
        int score = 0;
        for (QuizQuestion q : questions) {
            Object answer = answers.get(String.valueOf(q.getId()));
            if (answer != null && q.getCorrectOption() != null && q.getCorrectOption().equalsIgnoreCase(String.valueOf(answer))) {
                score++;
            }
        }
        QuizAttempt attempt = new QuizAttempt();
        attempt.setUserId(userId);
        attempt.setCourseId(courseId);
        attempt.setLessonId(lessonId);
        attempt.setScore(score);
        attempt.setTotal(questions.size());
        attempt.setSubmittedAt(LocalDateTime.now());
        return ResponseEntity.ok(attemptRepo.save(attempt));
    }

    @GetMapping("/attempts/user/{userId}")
    public List<QuizAttempt> attempts(@PathVariable int userId) {
        return attemptRepo.findByUserIdOrderBySubmittedAtDesc(userId);
    }

    private Map<String, Object> publicQuestion(QuizQuestion q) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id", q.getId());
        result.put("courseId", q.getCourseId());
        result.put("lessonId", q.getLessonId());
        result.put("question", q.getQuestion());
        result.put("optionA", q.getOptionA());
        result.put("optionB", q.getOptionB());
        result.put("optionC", q.getOptionC());
        result.put("optionD", q.getOptionD());
        return result;
    }

    private boolean canManage(String role) {
        return "ADMIN".equalsIgnoreCase(role) || "INSTRUCTOR".equalsIgnoreCase(role);
    }

    private ResponseEntity<?> forbidden() {
        return ResponseEntity.status(403).body(Map.of("success", false, "message", "Access denied."));
    }
}
