package com.bekaku.api.spring.controller.api;

import com.bekaku.api.spring.configuration.I18n;
import com.bekaku.api.spring.specification.SearchSpecification;
import com.bekaku.api.spring.model.SystemActivityLogs;
import com.bekaku.api.spring.service.SystemActivityLogsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.data.domain.Pageable;

import jakarta.validation.Valid;

import java.util.Optional;

@RequestMapping(path = "/api/systemActivityLogs")
@RestController
@RequiredArgsConstructor
public class SystemActivityLogsController extends BaseApiController {

    private final SystemActivityLogsService systemActivityLogsService;
    private final I18n i18n;

    @PreAuthorize("isHasPermission('system_activity_logs_list')")
    @GetMapping
    public ResponseEntity<Object> findAll(Pageable pageable) {
        SearchSpecification<SystemActivityLogs> specification = new SearchSpecification<>(getSearchCriteriaList());
        return this.responseEntity(systemActivityLogsService.findAllWithSearch(specification, getPageable(pageable, SystemActivityLogs.getSort())), HttpStatus.OK);
    }

    @PreAuthorize("isHasPermission('system_activity_logs_manage')")
    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody SystemActivityLogs systemActivityLogs) {
        systemActivityLogsService.save(systemActivityLogs);
        return this.responseEntity(systemActivityLogs, HttpStatus.OK);
    }

    @PreAuthorize("isHasPermission('system_activity_logs_manage')")
    @PutMapping
    public ResponseEntity<Object> update(@Valid @RequestBody SystemActivityLogs systemActivityLogs) {
        Optional<SystemActivityLogs> oldData = systemActivityLogsService.findById(systemActivityLogs.getId());
        if (oldData.isEmpty()) {
            throw this.responseErrorNotfound();
        }
        systemActivityLogsService.update(systemActivityLogs);
        return this.responseEntity(systemActivityLogs, HttpStatus.OK);
    }

    @PreAuthorize("isHasPermission('system_activity_logs_view')")
    @GetMapping("/{id}")
    public ResponseEntity<Object> findOne(@PathVariable("id") long id) {
        Optional<SystemActivityLogs> systemActivityLogs = systemActivityLogsService.findById(id);
        if (systemActivityLogs.isEmpty()) {
            throw this.responseErrorNotfound();
        }
        return this.responseEntity(systemActivityLogs.get(), HttpStatus.OK);
    }

    @PreAuthorize("isHasPermission('system_activity_logs_manage')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable("id") long id) {
        Optional<SystemActivityLogs> systemActivityLogs = systemActivityLogsService.findById(id);
        if (systemActivityLogs.isEmpty()) {
            throw this.responseErrorNotfound();
        }
        systemActivityLogsService.delete(systemActivityLogs.get());
        return this.responseDeleteMessage();
    }
}
