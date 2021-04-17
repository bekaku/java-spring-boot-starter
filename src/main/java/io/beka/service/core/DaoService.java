package io.beka.service.core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DaoService<T> extends AbstractService<T, Long> {

    @Autowired
    public DaoService(Dao<T, Long> dao) {
        super(dao);
    }

}