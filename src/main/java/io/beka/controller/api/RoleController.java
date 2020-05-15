package io.beka.controller.api;

import io.beka.exception.InvalidRequestException;
import io.beka.model.dto.RoleDto;
import io.beka.model.entity.Role;
import io.beka.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.HashMap;

@RequestMapping(path = "/api/role")
@RestController
public class RoleController {

    @Autowired
    private RoleService roleService;

    @PostMapping
    public ResponseEntity create(@Valid @RequestBody RoleDto dto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new InvalidRequestException(bindingResult);
        }

        Role role = new Role(dto.getName(), dto.getDescription());

        roleService.save(role);

        return ResponseEntity.ok(new HashMap<String, Object>() {{
            put("role", role);
        }});
    }
}
