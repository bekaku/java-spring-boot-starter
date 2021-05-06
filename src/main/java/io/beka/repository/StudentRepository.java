package io.beka.repository;

import io.beka.model.Course;
import io.beka.model.Student;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    List<Student> findByName(String name);

    @Query(value = "SELECT * FROM students s INNER JOIN students_courses sc ON s.id = sc.student_id WHERE sc.course_id =:courseId", nativeQuery = true)
    Page<Student> studentAllByCourseId(@Param("courseId") Long courseId, Pageable pageable);
}
