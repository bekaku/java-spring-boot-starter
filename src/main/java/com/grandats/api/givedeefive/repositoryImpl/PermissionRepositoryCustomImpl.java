package com.grandats.api.givedeefive.repositoryImpl;

import com.grandats.api.givedeefive.repository.PermissionRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PermissionRepositoryCustomImpl implements PermissionRepositoryCustom {

    private final EntityManager entityManager;


    public PermissionRepositoryCustomImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public void softDeleteById(Long id) {
        String queryStr = "UPDATE permission e SET deleted = true WHERE e.id=:id AND p.deleted = false";
        Query query = entityManager.createNativeQuery(queryStr);
        query.setParameter("id", id);
        query.executeUpdate();
    }

    @Override
    public Object[] findById(Long id) {
        String queryStr = "SELECT * FROM permission p WHERE p.id=:id AND p.deleted = false";
        Query query = entityManager.createNativeQuery(queryStr);
        query.setParameter("id", id);
        return (Object[]) query.getSingleResult();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Object[]> findAll() {
        String queryStr = "SELECT id, code, front_end FROM permission p WHERE p.deleted = false";
        Query query = entityManager.createNativeQuery(queryStr);
        return query.getResultList();
    }
}
