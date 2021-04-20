package io.beka.service;

import io.beka.dto.Paging;
import io.beka.dto.PermissionDto;
import io.beka.model.Permission;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public interface PermissonService extends BaseService<Permission, PermissionDto> {
    List<Permission> findAllViaMapper(Paging page);
}
