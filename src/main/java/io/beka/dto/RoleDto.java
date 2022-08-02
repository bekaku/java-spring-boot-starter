package io.beka.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonRootName;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDate;

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

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate expiredAt= LocalDate.now();

    private String description;
    private boolean status;
    private long[] selectdPermissions;
}
