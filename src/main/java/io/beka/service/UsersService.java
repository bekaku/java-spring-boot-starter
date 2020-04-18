package io.beka.service;

import io.beka.mapper.UsersMapper;
import io.beka.model.data.UserData;
import io.beka.model.entity.Users;
import io.beka.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UsersService {

    private final UsersRepository usersRepository;
    private final UsersMapper usersMapper;

    //Repository
    public Users save(Users users){
        return usersRepository.save(users);
    }
    public Optional<Users> findById(Long id){
        return usersRepository.findById(id);
    }
    public void delete(Users users){
        usersRepository.delete(users);
    }

    //Mapper
    public Optional<UserData> findUserDataById(Long id){
        return usersMapper.findById(id);
    }
    public Optional<UserData> findUserDataByUsername(String username){
        return usersMapper.findByUsername(username);
    }

    public Optional<UserData> findUserDataByEmail(String email){
        return usersMapper.findByEmail(email);
    }
}
