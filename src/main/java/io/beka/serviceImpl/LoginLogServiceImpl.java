package io.beka.serviceImpl;

import io.beka.vo.Paging;
import io.beka.dto.ResponseListDto;
import io.beka.model.LoginLog;
import io.beka.repository.LoginLogRepository;
import io.beka.service.LoginLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional
@Service
@RequiredArgsConstructor
public class LoginLogServiceImpl implements LoginLogService {

    private final LoginLogRepository loginLogRepository;

    @Override
    public ResponseListDto<LoginLog> findAllWithPaging(Paging paging, Sort sort) {
        return null;
    }

    @Override
    public List<LoginLog> findAll() {
        return loginLogRepository.findAll();
    }

    @Override
    public LoginLog save(LoginLog loginLog) {
        return loginLogRepository.save(loginLog);
    }

    @Override
    public LoginLog update(LoginLog loginLog) {
        return loginLogRepository.save(loginLog);
    }

    @Override
    public Optional<LoginLog> findById(Long id) {
        return loginLogRepository.findById(id);
    }

    @Override
    public void delete(LoginLog loginLog) {
        loginLogRepository.delete(loginLog);
    }

    @Override
    public void deleteById(Long id) {
        loginLogRepository.deleteById(id);
    }

    @Override
    public LoginLog convertEntityToDto(LoginLog loginLog) {
        return null;
    }

    @Override
    public LoginLog convertDtoToEntity(LoginLog loginLog) {
        return null;
    }
}
