package com.grandats.api.givedeefive.model;

import com.grandats.api.givedeefive.annotation.GenSourceableTable;
import com.grandats.api.givedeefive.model.superclass.Auditable;
import jakarta.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;
import org.springframework.data.domain.Sort;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;


@Getter
@Setter
@Entity
@GenSourceableTable(createPermission=false)
@NoArgsConstructor
@Table(name = "api_client", indexes = {
        @Index(columnList = "updated_user"),
        @Index(columnList = "created_user"),
})
public class ApiClient extends Auditable<Long> {

    public ApiClient(String apiName, Boolean byPass, Boolean status) {
        this.apiToken = UUID.randomUUID().toString();
        this.apiName = apiName;
        this.byPass = byPass;
        this.status = status;
    }

    public void update(String apiName, Boolean byPass, Boolean status) {
        if (!"".equals(apiName)) {
            this.apiName = apiName;
        }
        this.byPass = byPass;
        this.status = status;
    }

    @Column(name = "api_name", nullable = false, length = 100)
    private String apiName;

    @Column(unique = true)
    private String apiToken;

    @Column(columnDefinition = "tinyint(1) default 0")
    private Boolean byPass;

    @Column(columnDefinition = "tinyint(1) default 1")
    private Boolean status;

    // https://www.baeldung.com/jpa-cascade-types
    @OneToMany(mappedBy = "apiClient", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<ApiClientIp> apiClientIps= new HashSet<>();

    public static Sort getSort() {
        return Sort.by(Sort.Direction.ASC, "apiName");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ApiClient apiClient = (ApiClient) o;
        return getId() != null && Objects.equals(getId(), apiClient.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
