package io.beka.serviceImpl;

import io.beka.vo.Paging;
import io.beka.dto.ResponseListDto;
import io.beka.model.UserAgent;
import io.beka.repository.UserAgentRepository;
import io.beka.service.UserAgentService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional
@AllArgsConstructor
@Service
public class UserAgentServiceImpl implements UserAgentService {

    private final UserAgentRepository userAgentRepository;

    @Transactional(readOnly = true)
    @Override
    public ResponseListDto<UserAgent> findAllWithPaging(Paging paging, Sort sort) {
        return null;
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserAgent> findAll() {
        return userAgentRepository.findAll();
    }

    @Override
    public UserAgent save(UserAgent userAgent) {
        return userAgentRepository.save(userAgent);
    }

    @Override
    public UserAgent update(UserAgent userAgent) {
        return userAgentRepository.save(userAgent);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<UserAgent> findById(Long id) {
        return userAgentRepository.findById(id);
    }

    @Override
    public void delete(UserAgent userAgent) {
        userAgentRepository.delete(userAgent);
    }

    @Override
    public void deleteById(Long id) {
        userAgentRepository.deleteById(id);
    }

    @Override
    public UserAgent convertEntityToDto(UserAgent userAgent) {
        return null;
    }

    @Override
    public UserAgent convertDtoToEntity(UserAgent userAgent) {
        return null;
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<UserAgent> findByAgent(String name) {
        return userAgentRepository.findByAgent(name);
    }
}
