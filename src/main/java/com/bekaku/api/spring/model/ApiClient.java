package com.bekaku.api.spring.model;

import com.bekaku.api.spring.annotation.GenSourceableTable;
import com.bekaku.api.spring.model.superclass.Auditable;
import com.bekaku.api.spring.util.UuidUtils;
import jakarta.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;
import org.springframework.data.domain.Sort;

import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;


@Getter
@Setter
@Entity
@GenSourceableTable(createFrontend = true)
@NoArgsConstructor
@Table(name = "api_client", indexes = {
        @Index(columnList = "updated_user"),
        @Index(columnList = "created_user"),
        @Index(columnList = "app_user"),
})
public class ApiClient extends Auditable<Long> {

    public ApiClient(String apiName, Boolean byPass, Boolean status) {
        this.apiToken = UuidUtils.generateUUID().toString();
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

    private String apiTokenMask;

    private Boolean byPass= false;

    private Boolean status =true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "app_user", comment = "FK -> Ref table: app_user (id). Optional. Required only to authenticate this client with X-API-KEY")
    private AppUser appUser;

    @Column(name = "expires_at", comment = "Optional X-API-KEY expiry. Null never expires")
    private Instant expiresAt;

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
