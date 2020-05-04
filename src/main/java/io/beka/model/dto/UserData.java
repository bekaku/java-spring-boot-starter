package io.beka.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserData {
    private Long id;
    private String email;
    private String username;
    private String image;
    private Boolean status;
    private List<String> userRoles;
}
