package io.beka.model.dto;

import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@JsonRootName("role")
@NoArgsConstructor
@Getter
@AllArgsConstructor
public class RoleDto {
    private Long roleId;
    private String name;
    private String description;
    private boolean status;
}
