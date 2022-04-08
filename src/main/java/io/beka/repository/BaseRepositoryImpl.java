package io.beka.repository;

import io.beka.repository.BaseRepository;
import jakarta.persistence.EntityManager;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;

public class BaseRepositoryImpl <T, ID extends Serializable>
        extends SimpleJpaRepository<T, ID>  implements BaseRepository<T, ID> {

    private final EntityManager entityManager;

    public BaseRepositoryImpl(Class<T> domainClass, EntityManager entityManager) {
        super(domainClass, entityManager);
        this.entityManager = entityManager;
    }

    @Transactional
    @Override
    public void deleteById(ID id) {
        T deleted = entityManager.find(this.getDomainClass(), id);

        if (deleted != null) {
            entityManager.remove(deleted);
        }
    }

}
