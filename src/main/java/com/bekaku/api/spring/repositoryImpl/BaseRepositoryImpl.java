package com.bekaku.api.spring.repositoryImpl;

import jakarta.persistence.EntityManager;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import java.io.Serializable;

public class BaseRepositoryImpl<T, ID extends Serializable> extends SimpleJpaRepository<T, ID> {
    private final EntityManager entityManager;

    public BaseRepositoryImpl(Class<T> domainClass, EntityManager entityManager) {
        super(domainClass, entityManager);
        this.entityManager = entityManager;
    }
}
//public class BaseRepositoryImpl<T, ID extends Serializable> extends SimpleJpaRepository<T, ID> implements BaseRepository<T, ID> {
//    private final EntityManager entityManager;
//
//    public BaseRepositoryImpl(Class<T> domainClass, EntityManager entityManager) {
//        super(domainClass, entityManager);
//        this.entityManager = entityManager;
//    }
//
//    @Transactional
//    @Override
//    public void deleteById(ID id) {
//        T deleted = entityManager.find(this.getDomainClass(), id);
//        if (deleted != null) {
//            entityManager.remove(deleted);
//        }
//    }
//
//    @Override
//    public void softDeleteById(ID id) {
//        String queryStr = "UPDATE #{#entityName} e SET deleted = true WHERE e.id=?1";
//        Query query = entityManager.createNativeQuery(queryStr);
//        query.setParameter(1, id);
//        query.executeUpdate();
//    }
//}
