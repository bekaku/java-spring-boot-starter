package io.beka.controller.api;

import io.beka.configuration.I18n;
import io.beka.dto.UserDto;
import io.beka.dto.UserRegisterRequest;
import io.beka.model.Role;
import io.beka.model.User;
import io.beka.service.*;
import io.beka.vo.Paging;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@RequestMapping(path = "/api/user")
@RestController
public class UserController extends BaseApiController {

    private final UserService userService;
    private final RoleService roleService;
    private final EncryptService encryptService;
    private final I18n i18n;

    @Value("${app.default.image}")
    String defaultImage;

    @GetMapping("/current-user")
    public ResponseEntity<Object> currentUser(@AuthenticationPrincipal UserDto user) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        UserDto userDetails = (UserDto) authentication.getPrincipal();
        return this.responseEntity(user, HttpStatus.OK);
    }

    @PreAuthorize("isHasPermission('user_list')")
    @GetMapping
    public ResponseEntity<Object> findAll(Pageable pageable) {
        return this.responseEntity(userService.findAllWithPaging(!pageable.getSort().isEmpty() ? pageable :
                PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), User.getSort())), HttpStatus.OK);
    }

    @PreAuthorize("isHasPermission('user_add')")
    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody UserRegisterRequest dto) {
        validateUserRegister(dto);
        User user = new User(
                dto.getUsername(),
                dto.getPassword(),
                dto.getEmail(),
                true,
                defaultImage
        );
        setUserRoles(dto, user);
        //encrypt pwd
        user.setPassword(encryptService.encrypt(user.getPassword(), user.getSalt()));
        userService.save(user);
        return this.responseEntity(userService.convertEntityToDto(user), HttpStatus.OK);
    }

    private void setUserRoles(UserRegisterRequest dto, User user) {
        if (dto.getSelectedRoles().length > 0) {
            Optional<Role> role;
            for (long roleId : dto.getSelectedRoles()) {
                role = roleService.findById(roleId);
                role.ifPresent(value -> user.getRoles().add(value));
            }
        }
    }

    private void validateUserRegister(@RequestBody UserRegisterRequest dto) {

        List<String> errors = new ArrayList<>();
        if (userService.findByUsername(dto.getUsername()).isPresent()) {
            errors.add(i18n.getMessage("error.validateDuplicateUsername", dto.getUsername()));
        }
        if (userService.findByEmail(dto.getEmail()).isPresent()) {
            errors.add(i18n.getMessage("error.validateDuplicateEmail", dto.getEmail()));
        }
        if (errors.size() > 0) {
            throw this.responseError(HttpStatus.BAD_REQUEST, null, errors);
        }
    }

    @PreAuthorize("isHasPermission('user_view')")
    @GetMapping("/{id}")
    public ResponseEntity<Object> findOne(@PathVariable("id") long id) {
        Optional<UserDto> userData = userService.findUserDataById(id);
        if (userData.isEmpty()) {
            throw this.responseErrorNotfound();
        }
        return this.responseEntity(userData.get(), HttpStatus.OK);
    }

    @PreAuthorize("isHasPermission('user_edit')")
    @PutMapping("/{id}")
    public ResponseEntity<Object> updateUser(@PathVariable("id") long id, @Valid @RequestBody UserRegisterRequest dto) {
        Optional<User> user = userService.findById(id);
        if (user.isEmpty()) {
            throw this.responseErrorNotfound();
        }

        User userUpdate = user.get();
        userUpdate.update(
                dto.getUsername(),
                dto.getPassword(),
                dto.getEmail(),
                false,
                defaultImage
        );

        setUserRoles(dto, userUpdate);
        //encrypt pwd
        if (!ObjectUtils.isEmpty(userUpdate.getPassword())) {
            userUpdate.setPassword(encryptService.encrypt(userUpdate.getPassword(), userUpdate.getSalt()));
        }
        userService.update(userUpdate);
        return this.responseEntity(userService.convertEntityToDto(userUpdate), HttpStatus.OK);
    }

    @PreAuthorize("isHasPermission('user_delete')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable("id") long id) {
        Optional<User> user = userService.findById(id);
        if (user.isEmpty()) {
            throw this.responseErrorNotfound();
        }
        userService.deleteById(id);
        return this.responseEntity(HttpStatus.OK);
    }

}
