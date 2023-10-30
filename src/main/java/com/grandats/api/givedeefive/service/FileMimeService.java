package com.grandats.api.givedeefive.service;

import com.grandats.api.givedeefive.model.FileMime;

import java.util.Optional;

public interface FileMimeService extends BaseService<FileMime, FileMime> {
    Optional<FileMime> findByName(String name);
}
