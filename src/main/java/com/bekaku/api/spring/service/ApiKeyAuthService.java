package com.bekaku.api.spring.service;

import com.bekaku.api.spring.dto.AppUserDto;

import java.util.Optional;

/**
 * Authenticates a server-to-server caller from its raw X-API-KEY.
 *
 * <p>Kept separate from {@link ApiClientService} (which is CRUD and is already {@code @Lazy}
 * injected into AccessTokenServiceImpl to break a dependency cycle) because authentication is a
 * different concern and needs its own dependencies.
 */
public interface ApiKeyAuthService {

    /**
     * @param rawApiKey            raw value of the X-API-KEY header
     * @param acceptApiClientHeader raw value of the Accept-Apiclient header, cross-checked against
     *                              the api client's apiName
     * @return the principal to place in the SecurityContext, or empty when authentication fails.
     *         Always fails closed; never throws for a bad credential.
     */
    Optional<AppUserDto> authenticate(String rawApiKey, String acceptApiClientHeader);
}
