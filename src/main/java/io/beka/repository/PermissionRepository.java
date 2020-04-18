package io.beka.repository;

import io.beka.model.entity.Permissions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<Permissions, Long> {

    Optional<Permissions> findByName(String name);

    List<Permissions> findAllByCrudTable(String crudTable);

    @Query(value = "SELECT * FROM permissions p WHERE p.crud_table like ?1%", nativeQuery = true)
    List<Permissions> findAllNativeQuesruByLikeCrudTable(String crudTable);

    @Query("SELECT p FROM Permissions p WHERE p.name = ?1")
    Permissions findByQueryName(String name);
}
