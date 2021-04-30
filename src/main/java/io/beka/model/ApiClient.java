package io.beka.model;

import io.beka.annotation.GenSourceableTable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Sort;

import javax.persistence.*;
import java.util.*;

@Getter
@Setter
@Entity
@EqualsAndHashCode(of = {"apiToken"}, callSuper = false)
@GenSourceableTable
@NoArgsConstructor
public class ApiClient extends BaseEntity {

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

    @OneToMany(mappedBy = "apiClient", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<ApiClientIp> apiClientIps= new HashSet<>();

    public static Sort getSort() {
        return Sort.by(Sort.Direction.ASC, "apiName");
    }
}
