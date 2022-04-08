package io.beka.model;

import com.fasterxml.jackson.annotation.JsonRootName;
import io.beka.annotation.GenSourceableTable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.Hibernate;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.domain.Sort;

import java.util.Objects;

import static jakarta.persistence.FetchType.LAZY;


@GenSourceableTable(createDto = true)
@Data
@JsonRootName("apiClientIp")
@Entity
@NoArgsConstructor
public class ApiClientIp extends Auditable<Long> {

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ApiClientIp that = (ApiClientIp) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
