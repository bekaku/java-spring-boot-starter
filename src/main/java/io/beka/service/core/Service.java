package io.beka.service.core;

import java.io.Serializable;
import java.util.List;

@org.springframework.stereotype.Service
public interface Service<T, ID extends Serializable> {

    T save(T entity);

    List<T> findAll();

}