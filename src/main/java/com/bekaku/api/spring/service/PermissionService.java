package com.bekaku.api.spring.service;

import com.bekaku.api.spring.specification.SearchSpecification;
import com.bekaku.api.spring.dto.PermissionDto;
import com.bekaku.api.spring.vo.Paging;
import com.bekaku.api.spring.model.Permission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public interface PermissionService extends BaseService<Permission, PermissionDto> {

    List<PermissionDto> findAllLikeByCode(String code,Pageable pageable);

    List<Permission> findAllViaMapper(Paging page);

    boolean isHasPermission(long userId, String permissionCode);

    List<String> findAllPermissionCodeByUserId(@Param(value = "userId") Long userId);

    Optional<Permission> findByCode(String code);

    void saveAll(List<Permission> permissionList);

    Page<Permission> findAllWithSearchSpecification(SearchSpecification<Permission> specification, Pageable pageable);

    List<Long> findAllPermissionIdByRoleId(Long roleId);

    List<PermissionDto> findAllBy(Sort sort);

    // custom cuery
    Object[] findCustomById(Long id);

    List<Object[]> findAllCustom();

}
