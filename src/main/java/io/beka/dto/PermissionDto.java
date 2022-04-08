package io.beka.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonRootName;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import lombok.experimental.Accessors;

@Data
@JsonRootName("permission")
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Accessors(chain = true)
public class PermissionDto {
    private Long id;

    @NotEmpty(message = "{error.NotEmpty}")
    private String code;
    private String description;
    private String module;
}
