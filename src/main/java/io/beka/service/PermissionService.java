package io.beka.service;

import io.beka.vo.Paging;
import io.beka.dto.PermissionDto;
import io.beka.model.Permission;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;


@Service
public interface PermissionService extends BaseService<Permission, PermissionDto> {
    List<Permission> findAllViaMapper(Paging page);

    boolean isHasPermission(long userId, String permissionCode);

    Optional<Permission> findByCode(String code);
}
