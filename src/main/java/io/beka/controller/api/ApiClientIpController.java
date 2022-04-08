package io.beka.controller.api;

import io.beka.configuration.I18n;
import io.beka.dto.ApiClientIpDto;
import io.beka.model.ApiClient;
import io.beka.model.ApiClientIp;
import io.beka.service.ApiClientIpService;
import io.beka.service.ApiClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RequestMapping(path = "/api/apiClient/{apiClientId}/apiClientIp")
@RestController
@RequiredArgsConstructor
public class ApiClientIpController extends BaseApiController {

    private final ApiClientService apiClientService;
    private final ApiClientIpService apiClientIpService;
    private final I18n i18n;
    Logger logger = LoggerFactory.getLogger(ApiClientIpController.class);

    //api/apiClient/2/apiClientIp?page=0&size=2&sort=createdDate,desc
    @GetMapping
    public ResponseEntity<Object> findAll(Pageable pageable, @PathVariable("apiClientId") long apiClientId) {
        return this.responseEntity(apiClientIpService.findPageByApiClient(apiClientId,
                !pageable.getSort().isEmpty() ? pageable :
                        PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), ApiClientIp.getSort())),
                HttpStatus.OK);
//        return this.responseEntity(new HashMap<String, Object>() {{
//            put("apiClientId", apiClientId);
//            put("pageable", pageable);
//        }}, HttpStatus.OK);
    }

    //api/apiClient/2/apiClientIp
    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody ApiClientIpDto dto, @PathVariable("apiClientId") long apiClientId) {

        Optional<ApiClient> apiClient = apiClientService.findById(apiClientId);
        if (apiClient.isEmpty()) {
            throw this.responseErrorNotfound();
        }
        Optional<ApiClientIp> ipExist = apiClientIpService.findByApiClientIdAndIpAddress(apiClientId, dto.getIpAddress());
        if (ipExist.isPresent()) {
            throw this.responseErrorDuplicate(dto.getIpAddress());
        }

        ApiClientIp apiClientIp = apiClientIpService.convertDtoToEntity(dto);
        apiClientIp.setApiClient(apiClient.get());
        apiClientIpService.save(apiClientIp);
        return this.responseEntity(apiClientIpService.convertEntityToDto(apiClientIp), HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<Object> update(@Valid @RequestBody ApiClientIpDto dto) {
        ApiClientIp apiClientIp = apiClientIpService.convertDtoToEntity(dto);
        Optional<ApiClientIp> oldData = apiClientIpService.findById(dto.getId());
        if (oldData.isEmpty()) {
            throw this.responseErrorNotfound();
        }
        apiClientIpService.update(apiClientIp);
        return this.responseEntity(apiClientIpService.convertEntityToDto(apiClientIp), HttpStatus.OK);
    }

    //api/apiClient/2/apiClientIp/1
    @GetMapping("/{id}")
    public ResponseEntity<Object> findOne(@PathVariable("apiClientId") long apiClientId, @PathVariable("id") long id) {
        Optional<ApiClientIp> apiClientIp = apiClientIpService.findByIdAndApiClientId(id, apiClientId);
        if (apiClientIp.isEmpty()) {
            throw this.responseErrorNotfound();
        }
//        logger.info("ApiClient name : {}", apiClientIp.get().getApiClient().getApiName());
        return this.responseEntity(apiClientIpService.convertEntityToDto(apiClientIp.get()), HttpStatus.OK);
    }

    //api/apiClient/2/apiClientIp/1
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable("apiClientId") long apiClientId, @PathVariable("id") long id) {
        Optional<ApiClientIp> apiClientIp = apiClientIpService.findByIdAndApiClientId(id, apiClientId);
        if (apiClientIp.isEmpty()) {
            throw this.responseErrorNotfound();
        }
        apiClientIpService.deleteById(id);
        return this.responseEntity(HttpStatus.OK);
    }
}
