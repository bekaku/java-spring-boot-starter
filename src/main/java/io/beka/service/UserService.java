package io.beka.service;

import io.beka.mapper.UserMapper;
import io.beka.model.Page;
import io.beka.model.data.UserData;
import io.beka.model.entity.User;
import io.beka.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    //Repository
    public User save(User user){
        return userRepository.save(user);
    }
    public Optional<User> findById(Long id){
        return userRepository.findById(id);
    }
    public void delete(User user){
        userRepository.delete(user);
    }
    public Optional<User> findByEmail(String email){
        return userRepository.findByEmail(email);
    }
    public Optional<User> findByUsername(String username){
        return userRepository.findByUsername(username);
    }

    //Mapper
    public List<UserData> findAllUserData(Page page){
       return userMapper.findAll(page);
    }

    public Optional<UserData> findUserDataById(Long id){
        return userMapper.findById(id);
    }
    public Optional<UserData> findUserDataByUsername(String username){
        return userMapper.findByUsername(username);
    }

    public Optional<UserData> findUserDataByEmail(String email){
        return userMapper.findByEmail(email);
    }


    //test
    public String greet() {
        return "Hello, World";
    }
}
