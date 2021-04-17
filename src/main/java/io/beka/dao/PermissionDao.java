package io.beka.dao;

import io.beka.model.entity.Permission;
import io.beka.service.core.Dao;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissionDao extends Dao<Permission, Long> {
}
