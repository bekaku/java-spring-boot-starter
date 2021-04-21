package io.beka.service;

import io.beka.dto.Paging;
import io.beka.dto.PermissionDto;
import io.beka.dto.ResponseListDto;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

public interface BaseService<T, DTO> {

    ResponseListDto<DTO> findAllWithPaging(Paging paging, Sort sort);

    List<T> findAll();

    T save(T t);

    T update(T t);

    Optional<T> findById(Long id);

    void delete(T t);

    void deleteById(Long id);

    DTO convertEntityToDto(T t);

    T convertDtoToEntity(DTO dto);
}
