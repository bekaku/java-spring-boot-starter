package io.beka.model.entity;

import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Getter
@Entity
@EqualsAndHashCode(of = {"apiToken"})
@NoArgsConstructor
public class ApiClient {

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

    //    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private Long id;

    @Column(name = "api_name", nullable = false, length = 100)
    private String apiName;

    private String apiToken;

    @Column(columnDefinition = "tinyint(1) default 0")
    private Boolean byPass;

    @Column(columnDefinition = "tinyint(1) default 1")
    private Boolean status;

    @OneToMany(mappedBy = "apiClient", cascade = CascadeType.ALL)
    private List<ApiClientIp> apiClientIps;

    @CreationTimestamp
    private Date createdAt;

    @UpdateTimestamp
    private Date updatedAt;
}
