package io.beka.repository;

import io.beka.model.Permission;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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

    @Modifying
    @Query("UPDATE Permission p SET p.name = :name where p.id = :id")
    void updateName(@Param(value = "id") long id, @Param(value = "name") String name);
}
