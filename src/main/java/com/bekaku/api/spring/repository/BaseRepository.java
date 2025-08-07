package com.bekaku.api.spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;

@NoRepositoryBean
public interface BaseRepository<T, ID extends Serializable> extends JpaRepository<T, ID> {

//    @Modifying
//    void deleteById(ID id);
//
//    @Modifying
//    void softDeleteById(ID id);

//    @Query("delete from #{#entityName} e where e.id=?1")
//    @Modifying
//    void deleteById(ID id);

//    @Query("UPDATE #{#entityName} e SET deleted = true WHERE e.id=?1 AND deleted = false")
//    @Modifying
//    void softDeleteById(ID id);

//    @Query("select e from #{#entityName} e where e.deleted=0")
//    Page<T> findAllNotDeleted();
//
//    @Override
//    @Query(value = "select e from #{#entityName} e where e.deleted=0")
//    Page<T> findAll(Pageable pageable);
//
//    @Override
//    @Query(value = "select e from #{#entityName} e where e.deleted=0")
//    @NotNull
//    List<T> findAll();
//
//    @Query("select e from #{#entityName} e where e.deleted=1")
//    Page<T> findAllRecyclebin();
//
//    @Query("select e from #{#entityName} e where e.deleted=1")
//    Page<T> findAllRecyclebin(Pageable pageable);
//
//    @Query("update #{#entityName} e set e.deleted=true where e.id=?1")
//    @Modifying
//    void softDelete(String id);

}