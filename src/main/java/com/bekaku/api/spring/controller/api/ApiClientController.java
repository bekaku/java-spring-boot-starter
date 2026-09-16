package com.bekaku.api.spring.controller.api;

import com.bekaku.api.spring.configuration.I18n;
import com.bekaku.api.spring.dto.ApiClientDto;
import com.bekaku.api.spring.dto.ApiClientIpDto;
import com.bekaku.api.spring.dto.GeneratedApiKey;
import com.bekaku.api.spring.model.ApiClient;
import com.bekaku.api.spring.model.ApiClientIp;
import com.bekaku.api.spring.model.AppUser;
import com.bekaku.api.spring.model.Permission;
import com.bekaku.api.spring.service.ApiClientIpService;
import com.bekaku.api.spring.service.ApiClientService;
import com.bekaku.api.spring.service.AppUserService;
import com.bekaku.api.spring.specification.SearchSpecification;
import com.bekaku.api.spring.util.AppUtil;
import com.bekaku.api.spring.util.ControllerUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequestMapping(path = "/api/apiClient")
@RestController
@RequiredArgsConstructor
public class ApiClientController extends BaseApiController {

    private final ApiClientService apiClientService;
    private final ApiClientIpService apiClientIpService;
    private final AppUserService appUserService;
    private final I18n i18n;

    @PreAuthorize("@permissionChecker.hasPermission('api_client_list')")
    @GetMapping
    public ResponseEntity<Object> findAll(HttpServletRequest request, Pageable pageable) {
        SearchSpecification<ApiClient> specification = ControllerUtil.buildSpecification(request, List.of());
        return this.responseEntity(apiClientService.findAllWithSearch(specification, getPageable(pageable, Permission.getSort())), HttpStatus.OK);
    }

    @PreAuthorize("@permissionChecker.hasPermission('api_client_add')")
    @PostMapping("/generate/{apiId}")
    public String generate(@PathVariable("apiId") Long apiId) {
        Optional<ApiClient> apiClient = apiClientService.findById(apiId);
        if (apiClient.isEmpty()) {
            throw this.responseErrorNotfound();
        }
        GeneratedApiKey keyData= generateAndSetKey(apiClient.get());
        apiClientService.save(apiClient.get());
        return keyData.rawKey();
    }

    private GeneratedApiKey generateAndSetKey(ApiClient apiClient){
        GeneratedApiKey keyData = apiClientService.generateKey("sk_live", 32);
        String mask = AppUtil.maskSecretKey(keyData.rawKey());
        apiClient.setApiTokenMask(mask);
        apiClient.setApiToken(keyData.keyHash());

        return keyData;
    }
    @PreAuthorize("@permissionChecker.hasPermission('api_client_add')")
    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody ApiClientDto dto) {

        ApiClient apiClient = new ApiClient(dto.getApiName(), dto.getByPass(), dto.getStatus());
        Optional<ApiClient> apiExist = apiClientService.findByApiName(dto.getApiName());
        if (apiExist.isPresent()) {
            throw this.responseErrorDuplicate(dto.getApiName());
        }
        if (dto.getApiClientDtoList()!=null && !dto.getApiClientDtoList().isEmpty()) {
            for (ApiClientIpDto apiClientIpDto : dto.getApiClientDtoList()) {
                apiClient.getApiClientIps().add(new ApiClientIp(apiClient, apiClientIpDto.getIpAddress(), apiClientIpDto.getStatus()));
            }
        }
        applyOwnerAndExpiry(apiClient, dto);
        GeneratedApiKey keyData= generateAndSetKey(apiClient);
        apiClientService.save(apiClient);
        ApiClientDto dtoResponse = apiClientService.convertEntityToDto(apiClient);
        dtoResponse.setKey(keyData.rawKey());

        return this.responseEntity(dtoResponse, HttpStatus.OK);
    }

    @PreAuthorize("@permissionChecker.hasPermission('api_client_edit')")
    @PutMapping("/refreshToken/{apiClientId}")
    public ResponseEntity<Object> refreshApiToken(@PathVariable("apiClientId") long apiClientId) {
        Optional<ApiClient> apiClient = apiClientService.findById(apiClientId);
        if (apiClient.isEmpty()) {
            throw this.responseErrorNotfound();
        }
        // Must go through generateAndSetKey so api_token holds the SHA-256 hash that
        // X-API-KEY authentication looks up. The raw key is returned once, here only.
        GeneratedApiKey keyData = generateAndSetKey(apiClient.get());
        apiClientService.update(apiClient.get());
        ApiClientDto dtoResponse = apiClientService.convertEntityToDto(apiClient.get());
        dtoResponse.setKey(keyData.rawKey());
        return this.responseEntity(dtoResponse, HttpStatus.OK);
    }

    private void applyOwnerAndExpiry(ApiClient apiClient, ApiClientDto dto) {
        if (dto.getAppUserId() == null) {
            apiClient.setAppUser(null);
        } else {
            AppUser appUser = appUserService.findById(dto.getAppUserId())
                    .orElseThrow(() -> this.responseError(HttpStatus.BAD_REQUEST, "App user not found"));
            if (!appUser.isActive()) {
                throw this.responseError(HttpStatus.BAD_REQUEST, "App user is not active");
            }
            apiClient.setAppUser(appUser);
        }
        apiClient.setExpiresAt(dto.getExpiresAt());
    }

    private void validateDefaultApi(ApiClient apiClient) {
        String defultApi = "default";
        if (apiClient.getApiName().equals(defultApi)) {
            throw this.responseError(HttpStatus.BAD_REQUEST, "Cannot delete/update default API");
        }
    }

    @PreAuthorize("@permissionChecker.hasPermission('api_client_edit')")
    @PutMapping("/{apiClientId}")
    public ResponseEntity<Object> update(@PathVariable("apiClientId") long apiClientId, @Valid @RequestBody ApiClientDto dto) {

        Optional<ApiClient> oldData = apiClientService.findById(apiClientId);
        if (oldData.isEmpty()) {
            throw this.responseErrorNotfound();
        }
        ApiClient apiClient = oldData.get();
        validateDefaultApi(apiClient);
        if (!apiClient.getApiName().equals(dto.getApiName())) {
            Optional<ApiClient> apiExist = apiClientService.findByApiName(dto.getApiName());
            if (apiExist.isPresent()) {
                throw this.responseErrorDuplicate(dto.getApiName());
            }
        }
        // Mutate the managed entity. Converting the DTO would null out apiToken/apiTokenMask,
        // which ApiClientDto intentionally never carries.
        apiClient.update(dto.getApiName(), dto.getByPass(), dto.getStatus());
        applyOwnerAndExpiry(apiClient, dto);
        apiClientService.update(apiClient);
        return this.responseEntity(apiClientService.convertEntityToDto(apiClient), HttpStatus.OK);
    }

    @PreAuthorize("@permissionChecker.hasPermission('api_client_view')")
    @GetMapping("/{id}")
    public ResponseEntity<Object> findOne(@PathVariable("id") long id) {
        Optional<ApiClient> apiClient = apiClientService.findById(id);
        if (apiClient.isEmpty()) {
            throw this.responseErrorNotfound();
        }
        ApiClientDto dto = apiClientService.convertEntityToDto(apiClient.get());
        List<ApiClientIp> apiClientIps = apiClientIpService.findAllByApiClient(apiClient.get());
        List<ApiClientIpDto> apiClientIpDtos = new ArrayList<>();
        if (!apiClientIps.isEmpty()) {
            apiClientIpDtos = apiClientIps
                    .stream()
                    .map(apiClientIpService::convertEntityToDto)
                    .collect(Collectors.toList());
        }
        dto.setApiClientDtoList(apiClientIpDtos);
        return this.responseEntity(dto, HttpStatus.OK);
    }

    @PreAuthorize("@permissionChecker.hasPermission('api_client_delete')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable("id") long id) {
        Optional<ApiClient> apiClient = apiClientService.findById(id);
        if (apiClient.isEmpty()) {
            throw this.responseErrorNotfound();
        }
        validateDefaultApi(apiClient.get());
        apiClientService.delete(apiClient.get());
        return this.responseDeleteMessage();
    }
}
