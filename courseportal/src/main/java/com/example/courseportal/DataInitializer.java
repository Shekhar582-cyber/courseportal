package com.example.courseportal;

import com.example.courseportal.entity.Course;
import com.example.courseportal.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private CourseRepository courseRepo;

    @Override
    public void run(String... args) {
        if (courseRepo.count() > 0) return; // already seeded

        courseRepo.save(make("Web Development",    "web",      "Beginner",
            "Build full-stack apps with HTML, CSS, JavaScript, React & Node. From zero to job-ready developer.",
            "14 weeks", 120, 8420, 4.8, "Free", "🌐",
            "linear-gradient(135deg,#1e3a5f,#2563eb)", "badge-blue", "enroll-webdev.html"));

        courseRepo.save(make("Data Science",       "data",     "Intermediate",
            "Analyse data, create visualisations and extract powerful insights using Python, Pandas & Tableau.",
            "12 weeks", 95, 6200, 4.7, "Free", "📊",
            "linear-gradient(135deg,#064e3b,#10b981)", "badge-green", "enroll-datascience.html"));

        courseRepo.save(make("Machine Learning",   "ml",       "Advanced",
            "Explore AI, neural networks, and real-world ML model deployment using Python, TensorFlow & PyTorch.",
            "16 weeks", 110, 5100, 4.9, "Free", "🤖",
            "linear-gradient(135deg,#2e1065,#7c3aed)", "badge-purple", "enroll-ml.html"));

        courseRepo.save(make("Cloud Computing",    "cloud",    "All Levels",
            "Master AWS, Azure and Google Cloud. Deploy scalable applications with confidence.",
            "10 weeks", 85, 4800, 4.6, "Free", "☁️",
            "linear-gradient(135deg,#7c2d12,#ea580c)", "badge-orange", "enroll-cloud.html"));

        courseRepo.save(make("Cybersecurity",      "security", "Intermediate",
            "Learn ethical hacking, network security, penetration testing and vulnerability assessments.",
            "10 weeks", 78, 3600, 4.7, "Free", "🔐",
            "linear-gradient(135deg,#0f2027,#2c5364)", "badge-red", "#"));

        courseRepo.save(make("Mobile Development", "mobile",   "Beginner",
            "Build cross-platform iOS & Android apps with React Native and Flutter from scratch.",
            "12 weeks", 90, 2900, 4.5, "Free", "📱",
            "linear-gradient(135deg,#1a1a2e,#0f3460)", "badge-blue", "#"));

        courseRepo.save(make("UI/UX Design",       "web",      "Beginner",
            "Design stunning interfaces with Figma. Learn color theory, typography, and user-centered design.",
            "8 weeks", 65, 3200, 4.6, "Free", "🎨",
            "linear-gradient(135deg,#4a044e,#a21caf)", "badge-purple", "#"));

        courseRepo.save(make("DevOps & CI/CD",     "cloud",    "Advanced",
            "Master Docker, Kubernetes, Jenkins and GitHub Actions. Automate deployments end-to-end.",
            "10 weeks", 80, 2100, 4.8, "Free", "⚙️",
            "linear-gradient(135deg,#1c1917,#44403c)", "badge-orange", "#"));

        System.out.println("✅ Course Portal: 8 courses seeded successfully.");
    }

    private Course make(String title, String cat, String level, String desc,
                        String dur, int lessons, int students, double rating,
                        String price, String emoji, String gradient, String badge, String link) {
        Course c = new Course();
        c.setTitle(title);
        c.setCategory(cat);
        c.setLevel(level);
        c.setDescription(desc);
        c.setDuration(dur);
        c.setLessons(lessons);
        c.setStudents(students);
        c.setRating(rating);
        c.setPrice(price);
        c.setThumbEmoji(emoji);
        c.setThumbGradient(gradient);
        c.setBadge(badge);
        c.setLink(link);
        return c;
    }
}
