package io.beka.model.dto;

import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@JsonRootName("permision")
@NoArgsConstructor
@Getter
@AllArgsConstructor
public class PermissionDto {
    private Long id;
    private String name;
    private String description;
    private String crudTable;
    private boolean status;
}
