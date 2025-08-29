package com.bekaku.api.spring.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonRootName;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@JsonRootName("appRole")
@JsonIgnoreProperties(ignoreUnknown = true)
@Accessors(chain = true)
public class AppRoleDto extends DtoId {
    @NotEmpty(message = "{error.NotEmpty}")
    @Size(min = 3, max = 100, message = "{error.Size3Limit100}")
    private String name;
    private boolean active;
    private Long[] selectdPermissions = new Long[]{};
}
