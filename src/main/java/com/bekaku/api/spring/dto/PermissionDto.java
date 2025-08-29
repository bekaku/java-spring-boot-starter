package com.bekaku.api.spring.dto;

import com.bekaku.api.spring.enumtype.PermissionType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonRootName;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Getter
@Setter
@JsonRootName("permission")
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PermissionDto extends DtoId {
    @NotEmpty(message = "{error.NotEmpty}")
    private String code;
    private String remark;
    private String description;
    private PermissionType operationType;
}
