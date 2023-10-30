package com.bekaku.api.spring.service;

import com.bekaku.api.spring.model.FileMime;

import java.util.Optional;

public interface FileMimeService extends BaseService<FileMime, FileMime> {
    Optional<FileMime> findByName(String name);
}
