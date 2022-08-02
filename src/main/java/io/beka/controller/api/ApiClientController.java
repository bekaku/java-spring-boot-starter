package io.beka.controller.api;

import io.beka.configuration.I18n;
import io.beka.dto.ApiClientDto;
import io.beka.dto.ApiClientIpDto;
import io.beka.model.ApiClient;
import io.beka.model.ApiClientIp;
import io.beka.service.ApiClientIpService;
import io.beka.service.ApiClientService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RequestMapping(path = "/api/apiClient")
@RestController
@RequiredArgsConstructor
public class ApiClientController extends BaseApiController {

    private final ApiClientService apiClientService;
    private final ApiClientIpService apiClientIpService;
    private final I18n i18n;
    //   Logger logger = LoggerFactory.getLogger(ApiClientController.class);

    @PreAuthorize("isHasPermission('api_client_list')")
    @GetMapping
    public ResponseEntity<Object> findAll(Pageable pageable) {
        return this.responseEntity(apiClientService.findAllWithPaging(!pageable.getSort().isEmpty() ? pageable :
                PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), ApiClient.getSort())), HttpStatus.OK);
    }

    @PreAuthorize("isHasPermission('api_client_add')")
    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody ApiClientDto dto) {

//        return this.responseEntity(new HashMap<String, Object>() {{
//            put("dto", dto);
//        }}, HttpStatus.OK);
        ApiClient apiClient = new ApiClient(dto.getApiName(), dto.getByPass(), dto.getStatus());
        Optional<ApiClient> apiExist = apiClientService.findByApiName(dto.getApiName());
        if (apiExist.isPresent()) {
            throw this.responseErrorDuplicate(dto.getApiName());
        }
        if(dto.getApiClientDtoList().size()>0){
            for (ApiClientIpDto apiClientIpDto : dto.getApiClientDtoList()){
                apiClient.getApiClientIps().add(new ApiClientIp(apiClient, apiClientIpDto.getIpAddress(), apiClientIpDto.getStatus()));
            }
        }

        apiClientService.save(apiClient);
        return this.responseEntity(apiClientService.convertEntityToDto(apiClient), HttpStatus.OK);
    }

    @PreAuthorize("isHasPermission('api_client_edit')")
    @PutMapping("/refreshToken/{apiClientId}")
    public ResponseEntity<Object> refreshApiToken(@PathVariable("apiClientId") long apiClientId){
        Optional<ApiClient> apiClient = apiClientService.findById(apiClientId);
        if (apiClient.isEmpty()) {
            throw this.responseErrorNotfound();
        }
        apiClient.get().setApiToken(UUID.randomUUID().toString());
        apiClientService.update(apiClient.get());
        return this.responseEntity(HttpStatus.OK);
    }

    @PreAuthorize("isHasPermission('api_client_edit')")
    @PutMapping
    public ResponseEntity<Object> update(@Valid @RequestBody ApiClientDto dto) {

        ApiClient apiClient = apiClientService.convertDtoToEntity(dto);
        Optional<ApiClient> oldData = apiClientService.findById(dto.getId());
        if (oldData.isEmpty()) {
            throw this.responseErrorNotfound();
        }
        if (!oldData.get().getApiName().equals(apiClient.getApiName())) {
            Optional<ApiClient> apiExist = apiClientService.findByApiName(dto.getApiName());
            if (apiExist.isPresent()) {
                throw this.responseErrorDuplicate(dto.getApiName());
            }
        }
        apiClientService.update(apiClient);
        return this.responseEntity(apiClientService.convertEntityToDto(apiClient), HttpStatus.OK);
    }

    @PreAuthorize("isHasPermission('api_client_view')")
    @GetMapping("/{id}")
    public ResponseEntity<Object> findOne(@PathVariable("id") long id) {
        Optional<ApiClient> apiClient = apiClientService.findById(id);
        if (apiClient.isEmpty()) {
            throw this.responseErrorNotfound();
        }
        ApiClientDto dto = apiClientService.convertEntityToDto(apiClient.get());
        List<ApiClientIp> apiClientIps = apiClientIpService.findAllByApiClient(apiClient.get());
        List<ApiClientIpDto> apiClientIpDtos = new ArrayList<>();
        if (apiClientIps.size() > 0) {
            apiClientIpDtos = apiClientIps
                    .stream()
                    .map(apiClientIpService::convertEntityToDto)
                    .collect(Collectors.toList());
        }
        dto.setApiClientDtoList(apiClientIpDtos);
        return this.responseEntity(dto, HttpStatus.OK);
    }

    @PreAuthorize("isHasPermission('api_client_delete')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable("id") long id) {
        Optional<ApiClient> apiClient = apiClientService.findById(id);
        if (apiClient.isEmpty()) {
            throw this.responseErrorNotfound();
        }
        apiClientService.deleteById(id);
        return this.responseEntity(HttpStatus.OK);
    }
}
