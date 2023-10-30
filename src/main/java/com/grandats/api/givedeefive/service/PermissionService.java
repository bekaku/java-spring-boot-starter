package com.grandats.api.givedeefive.service;

import com.grandats.api.givedeefive.dto.PermissionDto;
import com.grandats.api.givedeefive.specification.SearchSpecification;
import com.grandats.api.givedeefive.vo.Paging;
import com.grandats.api.givedeefive.model.Permission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public interface PermissionService extends BaseService<Permission, PermissionDto> {

    List<Permission> findAllViaMapper(Paging page);

    boolean isHasPermission(long userId, String permissionCode);

    List<String> findAllPermissionCodeByUserId(@Param(value = "userId") Long userId, boolean frontend);

    Optional<Permission> findByCode(String code);

    void saveAll(List<Permission> permissionList);

    Page<Permission> findAllWithSearchSpecification(SearchSpecification<Permission> specification, Pageable pageable);

    List<Long> findAllPermissionIdByRoleId(Long roleId);

    List<PermissionDto> findAllBy(boolean frontEnd ,Sort sort);

    // custom cuery
    Object[] findCustomById(Long id);

    List<Object[]> findAllCustom();

}
