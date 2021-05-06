package io.beka.dto;

import lombok.Data;

@Data
public class CourseDto {

    private Long id;
    private String abbreviation;
    private String title;
    private int modules;
    private double fee;
}
