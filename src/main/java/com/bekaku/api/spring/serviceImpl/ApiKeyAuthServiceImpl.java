package com.bekaku.api.spring.serviceImpl;

import com.bekaku.api.spring.dto.AppUserDto;
import com.bekaku.api.spring.model.ApiClient;
import com.bekaku.api.spring.model.AppUser;
import com.bekaku.api.spring.repository.ApiClientRepository;
import com.bekaku.api.spring.service.ApiKeyAuthService;
import com.bekaku.api.spring.util.AppUtil;
import com.bekaku.api.spring.util.HashUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.util.Optional;

/**
 * Resolves an X-API-KEY to the app user that owns its api client.
 *
 * <p>The raw key is never logged and never stored: api_client.api_token holds
 * {@code HashUtil.sha256(rawKey)}, so the lookup is an indexed equality on the hash.
 */
@Slf4j
@Service
@Transactional(readOnly = true)
public class ApiKeyAuthServiceImpl implements ApiKeyAuthService {

    private final ApiClientRepository apiClients;
    private final Clock clock;

    @Autowired
    public ApiKeyAuthServiceImpl(ApiClientRepository apiClients) {
        this(apiClients, Clock.systemUTC());
    }

    ApiKeyAuthServiceImpl(ApiClientRepository apiClients, Clock clock) {
        this.apiClients = apiClients;
        this.clock = clock;
    }

    @Override
    public Optional<AppUserDto> authenticate(String rawApiKey, String acceptApiClientHeader) {
        if (AppUtil.isEmpty(rawApiKey) || AppUtil.isEmpty(acceptApiClientHeader)) {
            return Optional.empty();
        }

        Optional<ApiClient> found = apiClients.findActiveByApiTokenFetchAppUser(HashUtil.sha256(rawApiKey));
        if (found.isEmpty()) {
            log.warn("Api key rejected: unknown or disabled key");
            return Optional.empty();
        }
        ApiClient apiClient = found.get();

        if (!apiClient.getApiName().equals(acceptApiClientHeader.trim())) {
            log.warn("Api key rejected: Accept-Apiclient does not match apiClientId:{}", apiClient.getId());
            return Optional.empty();
        }
        if (apiClient.getExpiresAt() != null && !apiClient.getExpiresAt().isAfter(clock.instant())) {
            log.warn("Api key rejected: expired apiClientId:{}", apiClient.getId());
            return Optional.empty();
        }

        AppUser appUser = apiClient.getAppUser();
        if (appUser == null) {
            log.warn("Api key rejected: no app user linked to apiClientId:{}", apiClient.getId());
            return Optional.empty();
        }
        if (!appUser.isActive() || Boolean.TRUE.equals(appUser.getDeleted())) {
            log.warn("Api key rejected: inactive app user for apiClientId:{}", apiClient.getId());
            return Optional.empty();
        }

        AppUserDto principal = new AppUserDto();
        principal.setId(appUser.getId());
        log.info("Api key accepted apiClient:{}, UID:{}", apiClient.getApiName(), appUser.getId());
        return Optional.of(principal);
    }
}
