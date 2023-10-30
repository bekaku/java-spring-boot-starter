package com.bekaku.api.spring.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonRootName;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@JsonRootName("role")
@JsonIgnoreProperties(ignoreUnknown = true)
@Accessors(chain = true)
public class RoleDto {
    private Long id;

    //https://www.baeldung.com/javax-validation
    @NotEmpty(message = "{error.NotEmpty}")
    @Size(min = 3, max = 100, message = "{error.Size3Limit100}")
    private String name;
    private String nameEn;
    private boolean active;
    private boolean frontEnd;
    private Long[] selectdPermissions = new Long[]{};
    private Long companySelected;
}
