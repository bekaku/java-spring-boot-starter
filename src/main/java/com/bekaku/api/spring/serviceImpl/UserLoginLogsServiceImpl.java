package com.bekaku.api.spring.serviceImpl;

import com.bekaku.api.spring.dto.ResponseListDto;
import com.bekaku.api.spring.repository.UserLoginLogsRepository;
import com.bekaku.api.spring.specification.SearchSpecification;
import com.bekaku.api.spring.model.UserLoginLogs;
import com.bekaku.api.spring.service.UserLoginLogsService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional
@RequiredArgsConstructor
@Service
public class UserLoginLogsServiceImpl implements UserLoginLogsService {
    private final UserLoginLogsRepository userLoginLogsRepository;
    private final ModelMapper modelMapper;

    @Transactional(readOnly = true)
    @Override
    public ResponseListDto<UserLoginLogs> findAllWithPaging(Pageable pageable) {
        Page<UserLoginLogs> result = userLoginLogsRepository.findAll(pageable);
        return getListFromResult(result);
    }

    @Transactional(readOnly = true)
    @Override
    public ResponseListDto<UserLoginLogs> findAllWithSearch(SearchSpecification<UserLoginLogs> specification, Pageable pageable) {
        return getListFromResult(findAllPageSearchSpecificationBy(specification, pageable));
    }

    @Transactional(readOnly = true)
    @Override
    public ResponseListDto<UserLoginLogs> findAllBy(Specification<UserLoginLogs> specification, Pageable pageable) {
        return getListFromResult(findAllPageSpecificationBy(specification, pageable));
    }

    @Transactional(readOnly = true)
    @Override
    public Page<UserLoginLogs> findAllPageSpecificationBy(Specification<UserLoginLogs> specification, Pageable pageable) {
        return userLoginLogsRepository.findAll(specification, pageable);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<UserLoginLogs> findAllPageSearchSpecificationBy(SearchSpecification<UserLoginLogs> specification, Pageable pageable) {
        return userLoginLogsRepository.findAll(specification, pageable);
    }

    private ResponseListDto<UserLoginLogs> getListFromResult(Page<UserLoginLogs> result) {
        return new ResponseListDto<>(result.getContent()
                , result.getTotalPages(), result.getTotalElements(), result.isLast());
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserLoginLogs> findAll() {
        return userLoginLogsRepository.findAll();
    }


    public UserLoginLogs save(UserLoginLogs userLoginLogs) {
        return userLoginLogsRepository.save(userLoginLogs);
    }

    @Override
    public UserLoginLogs update(UserLoginLogs userLoginLogs) {
        return userLoginLogsRepository.save(userLoginLogs);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<UserLoginLogs> findById(Long id) {
        return userLoginLogsRepository.findById(id);
    }

    @Override
    public void delete(UserLoginLogs userLoginLogs) {
        userLoginLogsRepository.delete(userLoginLogs);
    }

    @Override
    public void deleteById(Long id) {
        userLoginLogsRepository.deleteById(id);
    }

    @Override
    public UserLoginLogs convertEntityToDto(UserLoginLogs userLoginLogs) {
        return userLoginLogs;
    }

    @Override
    public UserLoginLogs convertDtoToEntity(UserLoginLogs userLoginLogs) {
        return userLoginLogs;
    }

    @Override
    public List<UserLoginLogs> findByUserAndLoginDate(Long userId, String loginDateString) {
        return userLoginLogsRepository.findByUserAndLoginDate(userId, loginDateString);
    }
}
