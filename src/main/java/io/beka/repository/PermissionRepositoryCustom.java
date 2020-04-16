package io.beka.repository;

import io.beka.model.Page;
import io.beka.model.entity.Permissions;

import java.util.List;

public interface PermissionRepositoryCustom {

    List<Permissions> findCustomAllByCrudTableAndActive(String curdTable, Boolean status);

    List<Permissions> findAllPaging(Page page);

    List<Object[]> findAllByCustomObject();
}
