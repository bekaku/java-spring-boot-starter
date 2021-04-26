package io.beka.serviceImpl;

import io.beka.vo.Paging;
import io.beka.dto.ResponseListDto;
import io.beka.dto.UserData;
import io.beka.mapper.UserMapper;
import io.beka.model.User;
import io.beka.repository.UserRepository;
import io.beka.service.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Transactional
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final ModelMapper modelMapper;


    @Transactional(readOnly = true)
    @Override
    public ResponseListDto<UserData> findAllWithPaging(Paging paging, Sort sort) {
        Page<User> resault = userRepository.findAll(PageRequest.of(paging.getPage(), paging.getLimit(), sort));

        return new ResponseListDto<>(resault.getContent()
                .stream()
                .map(this::convertEntityToDto)
                .collect(Collectors.toList())
                , resault.getTotalPages(), resault.getNumberOfElements(), resault.isLast());
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
    public UserData convertEntityToDto(User user) {
        return modelMapper.map(user, UserData.class);
    }

    @Override
    public User convertDtoToEntity(UserData userData) {
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
    public List<UserData> findAllUserData(Paging page) {
        return userMapper.findAll(page);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<UserData> findUserDataById(Long id) {
        return userMapper.findById(id);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<UserData> findUserDataByUsername(String username) {
        return userMapper.findByUsername(username);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<UserData> findUserDataByEmail(String email) {
        return userMapper.findByEmail(email);
    }
}
