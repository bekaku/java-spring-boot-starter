package io.beka.model;

import com.fasterxml.jackson.annotation.JsonRootName;
import io.beka.annotation.TableSerializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;

@TableSerializable(createController = false)
@EqualsAndHashCode(callSuper = true)
@Data
@JsonRootName("apiClientIp")
@Getter
@Entity
public class ApiClientIp extends BaseEntity {

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "apiClient", nullable = false)
    private ApiClient apiClient;

    @Column(length = 50)
    private String ipAddress;

    @Column(columnDefinition = "tinyint(1) default 1")
    private Boolean status;

}
