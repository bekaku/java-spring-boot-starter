package com.grandats.api.givedeefive.serviceImpl;

import com.grandats.api.givedeefive.configuration.I18n;
import com.grandats.api.givedeefive.dto.*;
import com.grandats.api.givedeefive.exception.BaseResponseException;
import com.grandats.api.givedeefive.mapper.UserMapper;
import com.grandats.api.givedeefive.model.*;
import com.grandats.api.givedeefive.repository.UserRepository;
import com.grandats.api.givedeefive.service.FileManagerService;
import com.grandats.api.givedeefive.service.UserService;
import com.grandats.api.givedeefive.specification.SearchSpecification;
import com.grandats.api.givedeefive.vo.Paging;
import org.modelmapper.ModelMapper;
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
    private UserMapper userMapper;
    @Autowired
    private FileManagerService fileManagerService;

    @Autowired
    private I18n i18n;

    @Autowired
    private ModelMapper modelMapper;
    Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Transactional(readOnly = true)
    @Override
    public ResponseListDto<UserDto> findAllWithPaging(Pageable pageable) {
        Page<User> result = userRepository.findAll(pageable);
        return getListFromResult(result);
    }

    @Override
    public ResponseListDto<UserDto> findAllWithSearch(SearchSpecification<User> specification, Pageable pageable) {
        Page<User> result = userRepository.findAll(specification, pageable);
        return getListFromResult(result);
    }

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
                , result.getTotalPages(), result.getNumberOfElements(), result.isLast());
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
        UserDto dto = modelMapper.map(user, UserDto.class);
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
        return modelMapper.map(userData, User.class);
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
        return userMapper.findAll(page);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<UserDto> findUserDataById(Long id) {
        return userMapper.findById(id);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<UserDto> findUserDataByUsername(String username) {
        return userMapper.findByUsername(username);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<UserDto> findUserDataByEmail(String email) {
        return userMapper.findByEmail(email);
    }

    @Override
    public void requireTheSameUser(Long leftUserId, Long rightUserId) {
        if (!Objects.equals(leftUserId, rightUserId)) {
            throw this.responseErrorUnauthorized();
        }
    }

    @Override
    public void requireTheSameUser(User leftUser, User rightUser) {
        if (!Objects.equals(leftUser.getId(), rightUser.getId())) {
            throw this.responseErrorUnauthorized();
        }
    }
}
