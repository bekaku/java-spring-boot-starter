package io.beka.service.core;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.io.Serializable;

@Repository
public interface Dao<T, ID extends Serializable> extends JpaRepository<T, ID> {


}