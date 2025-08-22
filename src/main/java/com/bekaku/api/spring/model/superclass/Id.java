package com.bekaku.api.spring.model.superclass;

import com.bekaku.api.spring.util.SnowflakeIdHolder;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.io.Serializable;

@MappedSuperclass
@Getter
@Setter
public class Id implements Serializable {

    @jakarta.persistence.Id
    private Long id;

    @PrePersist
    public void generateId() {
        if (this.id == null) {
            this.id = SnowflakeIdHolder.generator().nextId();
        }
    }

    /*
    AUTO	Hibernate chooses the generation strategy based on the dialect (default).
    IDENTITY	Uses database identity column (e.g. AUTO_INCREMENT in MySQL).
    SEQUENCE	Uses a database sequence (used by PostgreSQL, Oracle).
    TABLE	Uses a table to simulate sequence-like behavior.
     */
//    @jakarta.persistence.Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;

//    @jakarta.persistence.Id
//    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
//    @GenericGenerator(name = "native", strategy = "native")
//    private Long id;

}
