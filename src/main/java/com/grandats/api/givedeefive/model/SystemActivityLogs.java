package com.grandats.api.givedeefive.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.grandats.api.givedeefive.annotation.GenSourceableTable;
import com.grandats.api.givedeefive.model.superclass.Id;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Sort;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@GenSourceableTable(createDto = false, createPermission = false)
@Getter
@Setter
@Entity
@JsonRootName("postDataReview")
public class SystemActivityLogs extends Id {

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(columnDefinition = "datetime")
    private LocalDateTime actionDateTime;

    public static Sort getSort() {
        return Sort.by(Sort.Direction.DESC, "id");
    }
}
