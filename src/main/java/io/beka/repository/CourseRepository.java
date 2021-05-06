package io.beka.repository;

import io.beka.model.Course;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long>, JpaSpecificationExecutor<Course> {

    List<Course> findByTitleContaining(String title);

    List<Course> findByFeeLessThan(double fee);

    @Query(value = "SELECT * FROM courses c INNER JOIN students_courses sc ON c.id = sc.course_id WHERE sc.student_id =:studentId", nativeQuery = true)
    Page<Course> courseAllByStudentId(@Param("studentId") Long studentId, Pageable pageable);
}
