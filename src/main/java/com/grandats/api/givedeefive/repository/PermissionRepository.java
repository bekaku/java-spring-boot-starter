package com.grandats.api.givedeefive.repository;

import com.grandats.api.givedeefive.model.Permission;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


/**
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

    List<Permission> findAllByfrontEnd(boolean frontEnd, Sort sort);

    @Query(value = "SELECT p.code FROM user_role ur LEFT JOIN role_permission pr ON ur.role = pr.role "
            + " LEFT JOIN permission p ON pr.permission = p.id WHERE p.deleted =0 AND ur.user = :userId AND p.`code` = :code "
            , nativeQuery = true)
    List<String> findPermissionsByUserIdAndPermissionCode(@Param(value = "userId") long userId, @Param(value = "code") String code);

    @Query(value = "SELECT DISTINCT(p.`code`) FROM user_role ur LEFT JOIN role_permission pr ON ur.role = pr.role "
            + " LEFT JOIN permission p ON pr.permission = p.id WHERE p.deleted =0 AND ur.user = ?1 AND p.front_end is false "
            , nativeQuery = true)
    List<String> findAllBackendPermissionCodeByUserId(Long userId);

    @Query(value = "SELECT DISTINCT(p.`code`) FROM user_role ur LEFT JOIN role_permission pr ON ur.role = pr.role "
            + " LEFT JOIN permission p ON pr.permission = p.id WHERE p.deleted =0 AND ur.user = ?1 AND p.front_end is true "
            , nativeQuery = true)
    List<String> findAllFrontendPermissionCodeByUserId(Long userId);

    @Query(value = "SELECT p.id FROM permission p INNER JOIN role_permission rp ON p.id = rp.permission WHERE rp.role =:roleId", nativeQuery = true)
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
