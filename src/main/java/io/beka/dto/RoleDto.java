package io.beka.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

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

    private String description;
    private boolean status;
    private long[] selectdPermissions;
}
