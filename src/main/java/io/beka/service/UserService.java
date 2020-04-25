package io.beka.service;

import io.beka.mapper.UserMapper;
import io.beka.model.data.UserData;
import io.beka.model.entity.User;
import io.beka.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UsersRepository usersRepository;
    private final UserMapper userMapper;

    //Repository
    public User save(User user){
        return usersRepository.save(user);
    }
    public Optional<User> findById(Long id){
        return usersRepository.findById(id);
    }
    public void delete(User user){
        usersRepository.delete(user);
    }

    //Mapper
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
