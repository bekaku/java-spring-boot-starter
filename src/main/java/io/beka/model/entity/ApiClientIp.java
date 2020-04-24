package io.beka.model.entity;

import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Data;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;

@Data
@JsonRootName("apiClientIp")
@Getter
@Entity
public class ApiClientIp {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "apiClient")
    private ApiClient apiClient;

    @Column(columnDefinition = "tinyint(1) default 1")
    private Boolean status;

    @CreationTimestamp
    private Date createdAt;

    @UpdateTimestamp
    private Date updatedAt;
}
