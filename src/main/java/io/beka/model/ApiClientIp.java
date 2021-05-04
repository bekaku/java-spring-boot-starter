package io.beka.model;

import com.fasterxml.jackson.annotation.JsonRootName;
import io.beka.annotation.GenSourceableTable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.domain.Sort;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;

@GenSourceableTable(createDto = true)
@EqualsAndHashCode(callSuper = true)
@Data
@JsonRootName("apiClientIp")
@Entity
@NoArgsConstructor
public class ApiClientIp extends BaseEntity {

    public ApiClientIp(ApiClient apiClient,String ipAddress, Boolean status) {
        this.apiClient = apiClient;
        this.ipAddress = ipAddress;
        this.status = status;
    }

    @ManyToOne(fetch = LAZY, optional = false)
    @JoinColumn(name = "apiClient", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private ApiClient apiClient;

    @Column(length = 50)
    private String ipAddress;

    @Column(columnDefinition = "tinyint(1) default 1")
    private Boolean status;

    public static Sort getSort() {
        return Sort.by(Sort.Direction.ASC, "ipAddress");
    }

}
