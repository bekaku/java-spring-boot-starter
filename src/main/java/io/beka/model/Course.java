package io.beka.model;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "courses")
public class Course implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty
    private String title;

    @NotEmpty
    private String abbreviation;

    @NotNull
    private int modules;

    @NotNull
    private double fee;

    @ManyToMany(mappedBy = "courses", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private Set<Student> students = new HashSet<>();

    public Course(String title, String abbreviation, int modules, double fee) {
        this.title = title;
        this.abbreviation = abbreviation;
        this.modules = modules;
        this.fee = fee;
    }
}
