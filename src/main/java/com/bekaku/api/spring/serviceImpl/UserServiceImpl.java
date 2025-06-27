package com.bekaku.api.spring.serviceImpl;

import com.bekaku.api.spring.configuration.I18n;
import com.bekaku.api.spring.dto.ImageDto;
import com.bekaku.api.spring.dto.ResponseListDto;
import com.bekaku.api.spring.dto.UserDto;
import com.bekaku.api.spring.exception.BaseResponseException;
import com.bekaku.api.spring.mapper.UserMapper;
import com.bekaku.api.spring.model.Role;
import com.bekaku.api.spring.model.User;
import com.bekaku.api.spring.mybatis.UserMybatis;
import com.bekaku.api.spring.repository.UserRepository;
import com.bekaku.api.spring.service.FileManagerService;
import com.bekaku.api.spring.service.UserService;
import com.bekaku.api.spring.specification.SearchSpecification;
import com.bekaku.api.spring.vo.Paging;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Transactional
@Service
public class UserServiceImpl extends BaseResponseException implements UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserMybatis userMybatis;
    @Autowired
    private FileManagerService fileManagerService;

    @Autowired
    private I18n i18n;

    @Autowired
    private UserMapper modelMapper;
    Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Transactional(readOnly = true)
    @Override
    public ResponseListDto<UserDto> findAllWithPaging(Pageable pageable) {
        Page<User> result = userRepository.findAll(pageable);
        return getListFromResult(result);
    }

    @Transactional(readOnly = true)
    @Override
    public ResponseListDto<UserDto> findAllWithSearch(SearchSpecification<User> specification, Pageable pageable) {
        Page<User> result = userRepository.findAll(specification, pageable);
        return getListFromResult(result);
    }

    @Transactional(readOnly = true)
    @Override
    public ResponseListDto<UserDto> findAllBy(Specification<User> specification, Pageable pageable) {
        return null;
    }

    @Override
    public Page<User> findAllPageSpecificationBy(Specification<User> specification, Pageable pageable) {
        return null;
    }

    @Override
    public Page<User> findAllPageSearchSpecificationBy(SearchSpecification<User> specification, Pageable pageable) {
        return null;
    }

    private ResponseListDto<UserDto> getListFromResult(Page<User> result) {
        return new ResponseListDto<>(result.getContent().stream().map(this::convertEntityToDto).collect(Collectors.toList())
                , result.getTotalPages(), result.getTotalElements(), result.isLast());
    }

    @Transactional(readOnly = true)
    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public User update(User user) {
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public void delete(User user) {
        userRepository.delete(user);
    }

    @Override
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public UserDto convertEntityToDto(User user) {
        UserDto dto = modelMapper.toDto(user);
        if (user.getAvatarFile() != null) {
            Optional<ImageDto> imageDto = fileManagerService.findImageDtoBy(user.getAvatarFile());
            imageDto.ifPresent(dto::setAvatar);
        } else {
            dto.setAvatar(fileManagerService.getDefaultAvatar());
        }
        if (user.getCoverFile() != null) {
            Optional<ImageDto> imageDto = fileManagerService.findImageDtoBy(user.getCoverFile());
            imageDto.ifPresent(dto::setCover);
        }
        if (!user.getRoles().isEmpty()) {
            List<Long> rolesId = new ArrayList<>();
            for (Role r : user.getRoles()) {
                rolesId.add(r.getId());
            }
            dto.setSelectedRoles(rolesId);
        }
        return dto;
    }


    @Override
    public User convertDtoToEntity(UserDto userData) {
        return modelMapper.toEntity(userData);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<User> findByUUID(String salt) {
        return userRepository.findBySalt(salt);
    }

    @Override
    public void updatePasswordBy(User user, String password) {
        userRepository.updatePasswordBy(user, password);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    //mapper
    @Transactional(readOnly = true)
    @Override
    public List<UserDto> findAllUserData(Paging page) {
        return userMybatis.findAll(page);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<UserDto> findUserDataById(Long id) {
        return userMybatis.findById(id);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<UserDto> findUserDataByUsername(String username) {
        return userMybatis.findByUsername(username);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<UserDto> findUserDataByEmail(String email) {
        return userMybatis.findByEmail(email);
    }

    @Override
    public void requireTheSameUser(Long leftUserId, Long rightUserId) {
        if (!Objects.equals(leftUserId, rightUserId)) {
            throw this.responseErrorForbidden();
        }
    }

    @Override
    public void requireTheSameUser(User leftUser, User rightUser) {
        if (!Objects.equals(leftUser.getId(), rightUser.getId())) {
            throw this.responseErrorForbidden();
        }
    }
}
