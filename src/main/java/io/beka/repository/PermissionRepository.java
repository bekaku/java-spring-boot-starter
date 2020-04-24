package io.beka.repository;

import io.beka.model.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {

    Optional<Permission> findByName(String name);

    List<Permission> findAllByCrudTable(String crudTable);

    @Query(value = "SELECT * FROM permission p WHERE p.crud_table like ?1%", nativeQuery = true)
    List<Permission> findAllNativeQuesruByLikeCrudTable(String crudTable);

    @Query("SELECT p FROM Permission p WHERE p.name = ?1")
    Permission findByQueryName(String name);
}
