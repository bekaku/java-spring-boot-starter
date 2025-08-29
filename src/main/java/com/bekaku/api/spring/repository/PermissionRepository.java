package com.bekaku.api.spring.repository;

import com.bekaku.api.spring.model.Permission;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
/*
 * //https://www.baeldung.com/spring-data-jpa-query
 * //https://docs.spring.io/spring-data/jpa/docs/current/reference/html/
 * //https://www.bezkoder.com/spring-jpa-query/
 */
@Repository
public interface PermissionRepository extends BaseRepository<Permission, Long>, JpaSpecificationExecutor<Permission> {

    Optional<Permission> findByCode(String code);

//    List<Permission> findAllByModule(String module);

//    @Query(value = "SELECT * FROM permission p WHERE p.module like ?1%", nativeQuery = true)
//    List<Permission> findAllNativeQuesruByLikeModule(String module);

    @Query("SELECT e FROM Permission e WHERE e.code LIKE %?1%")
    List<Permission> findAllLikeByCode(String code, Pageable pageable);

    List<Permission> findAllBy(Sort sort);

    @Query(value = "SELECT p.code FROM app_user_role ur LEFT JOIN role_permission pr ON ur.app_role = pr.app_role "
            + " LEFT JOIN permission p ON pr.permission = p.id WHERE ur.app_user = :userId AND p.code = :code "
            , nativeQuery = true)
    List<String> findPermissionsByUserIdAndPermissionCode(@Param(value = "userId") long userId, @Param(value = "code") String code);

    @Query(value = "SELECT DISTINCT(p.code) FROM app_user_role ur LEFT JOIN role_permission pr ON ur.app_role = pr.app_role "
            + " LEFT JOIN permission p ON pr.permission = p.id WHERE ur.app_user = ?1 "
            , nativeQuery = true)
    List<String> findAllPermissionCodeByUserId(Long userId);


    @Query(value = "SELECT p.id FROM permission p INNER JOIN role_permission rp ON p.id = rp.permission WHERE rp.app_role =:roleId", nativeQuery = true)
    List<Long> findAllPermissionIdByRoleId(@Param("roleId") Long roleId);

    @Query("SELECT p FROM Permission p WHERE p.code = ?1")
    Permission findByQueryCode(String code);

    @Modifying
    @Query("UPDATE Permission p SET p.code = :code where p.id = :id")
    void updateName(@Param(value = "id") long id, @Param(value = "code") String code);

    @Modifying
    @Query("DELETE from Permission p where p.code = :code")
    void deletePermission(@Param("code") String code);
}
