package io.beka.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@JsonRootName("studentCourse")
@JsonIgnoreProperties(ignoreUnknown = true)
@Accessors(chain = true)
public class StudentCourseDto {
    private Long studentId;
    private long[] selectdCourses;
}
