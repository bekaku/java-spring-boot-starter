package com.grandats.api.givedeefive.repository;

import com.grandats.api.givedeefive.model.FileMime;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FileMimeRepository extends BaseRepository<FileMime, Long>, JpaSpecificationExecutor<FileMime> {
    Optional<FileMime> findByName(String name);
}
