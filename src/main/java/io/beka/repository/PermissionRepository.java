package io.beka.repository;

import io.beka.model.Permission;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PermissionRepository extends BaseRepository<Permission, Long> {

    Optional<Permission> findByCode(String code);

    List<Permission> findAllByModule(String module);

    @Query(value = "SELECT * FROM permission p WHERE p.module like ?1%", nativeQuery = true)
    List<Permission> findAllNativeQuesruByLikeModule(String module);

    @Query(value = "SELECT p.code FROM user_role ur LEFT JOIN role_permission pr ON ur.role = pr.role "
            + " LEFT JOIN permission p ON pr.permission = p.id WHERE ur.user = :userId AND p.`code` = :code "
            , nativeQuery = true)
    List<String> findPermissionsByUserIdAndPermissionCode(@Param(value = "userId") long userId, @Param(value = "code") String code);

    @Query("SELECT p FROM Permission p WHERE p.code = ?1")
    Permission findByQueryCode(String code);

    @Modifying
    @Query("UPDATE Permission p SET p.code = :code where p.id = :id")
    void updateName(@Param(value = "id") long id, @Param(value = "code") String code);
}
