package com.bekaku.api.spring.model;

import com.bekaku.api.spring.annotation.GenSourceableTable;
import com.bekaku.api.spring.model.superclass.Created;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@GenSourceableTable(createPermission = false, createController = false, createDto = false)
@Entity
@Table(name = "unanswered_prompt_log", comment="Unanswered Prompt Log")
@Getter
@Setter
@NoArgsConstructor
public class UnansweredPromptLog extends Created {

    @Column(name = "user_id", nullable = false, comment = "The ID of the user who asked the question.")
    private Long userId;

    @Column(name = "prompt", columnDefinition = "TEXT", nullable = false, comment = "A question not answered in Vector DB.")
    private String prompt;

    @Column(name = "is_acknowledged", nullable = false, comment = "Has the admin received this? (false=no, true=received)")
    private boolean acknowledged = false;
}
