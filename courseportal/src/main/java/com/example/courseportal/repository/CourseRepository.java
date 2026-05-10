package com.example.courseportal.repository;

import com.example.courseportal.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Integer> {
    List<Course> findByCategoryAndPublishedTrue(String category);
    List<Course> findByInstructorUserId(Integer instructorUserId);

    @Query("SELECT c FROM Course c WHERE " +
           "(:q IS NULL OR LOWER(c.title) LIKE LOWER(CONCAT('%',:q,'%')) " +
           "   OR LOWER(c.description) LIKE LOWER(CONCAT('%',:q,'%'))) AND " +
           "(:cat IS NULL OR :cat = 'all' OR c.category = :cat) AND " +
           "(:level IS NULL OR :level = 'all' OR c.level = :level)")
    List<Course> search(@Param("q") String q,
                        @Param("cat") String cat,
                        @Param("level") String level);

    @Query("SELECT c.category, COUNT(e.id) FROM Course c LEFT JOIN Enrollment e ON e.courseId = c.id GROUP BY c.category")
    List<Object[]> enrollmentCountByCategory();
}
