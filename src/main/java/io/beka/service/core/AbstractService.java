package io.beka.service.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.List;

public abstract class AbstractService<T, ID extends Serializable> implements Service<T, ID> {

    protected final Logger logger = LoggerFactory.getLogger(getClass());
    protected Dao<T, ID> dao;

    public AbstractService(Dao<T, ID> dao) {
        this.dao = dao;
    }

    @Override
    public T save(T entity) {
        this.logger.debug("Create a new {} with information: {}", entity.getClass(), entity.toString());
        return this.dao.save(entity);
    }

    @Override
    public List<T> findAll() {
        return this.dao.findAll();
    }

}