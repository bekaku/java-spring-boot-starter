package io.beka.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.*;
import lombok.experimental.Accessors;

@Data
@JsonRootName("permision")
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Accessors(chain = true)
public class PermissionDto {
    private Long id;
    private String name;
    private String description;
    private String crudTable;
    private boolean status;
}
