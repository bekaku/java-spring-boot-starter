package io.beka.repository;

import io.beka.model.entity.ApiClient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ApiClientRepository extends JpaRepository<ApiClient, Long> {

    Optional<ApiClient> findByApiName(String apiName);
}