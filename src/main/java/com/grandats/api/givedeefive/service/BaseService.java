package com.grandats.api.givedeefive.service;

import com.grandats.api.givedeefive.dto.ResponseListDto;
import com.grandats.api.givedeefive.specification.SearchSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

public interface BaseService<T, DTO> {

//    ResponseListDto<DTO> findAllWithPaging(Paging paging, Sort sort);
    ResponseListDto<DTO> findAllWithPaging(Pageable pageable);

    ResponseListDto<DTO> findAllWithSearch(SearchSpecification<T> specification, Pageable pageable);

    ResponseListDto<DTO> findAllBy(Specification<T> specification, Pageable pageable);

    Page<T> findAllPageSpecificationBy(Specification<T> specification, Pageable pageable);

    Page<T> findAllPageSearchSpecificationBy(SearchSpecification<T> specification, Pageable pageable);

    /*
    Page<Permission> findAllWithSearchSpecification(SearchSpecification<Permission> specification, Pageable pageable);
     */

    List<T> findAll();

    T save(T t);

    T update(T t);

    Optional<T> findById(Long id);

    void delete(T t);

    void deleteById(Long id);

    DTO convertEntityToDto(T t);

    T convertDtoToEntity(DTO dto);
}
