package com.bekaku.api.spring.controller.api;

import com.bekaku.api.spring.configuration.I18n;
import com.bekaku.api.spring.dto.ResponseListDto;
import com.bekaku.api.spring.dto.TestTempDto;
import com.bekaku.api.spring.model.TestTemp;
import com.bekaku.api.spring.service.TestTempService;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import com.bekaku.api.spring.specification.SearchSpecification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.data.domain.Pageable;
import com.bekaku.api.spring.util.ControllerUtil;
import jakarta.servlet.http.HttpServletRequest;
import com.bekaku.api.spring.util.ConstantData;

import jakarta.validation.Valid;
import java.util.Optional;
import java.util.List;
import org.springframework.http.HttpStatus;

@Slf4j
@RequestMapping(path = "/api/testTemp")
@RestController
@RequiredArgsConstructor
public class TestTempController extends BaseApiController{

    private final TestTempService testTempService;
    private final I18n i18n;

    @GetMapping
    public ResponseEntity<ResponseListDto<TestTempDto>> findAll(HttpServletRequest request, Pageable pageable) {
        SearchSpecification<TestTemp> specification = ControllerUtil.buildSpecification(request, List.of());
        return this.responseEntity(testTempService.findAllWithSearch(specification, getPageable(pageable, TestTemp.getSort())), HttpStatus.OK);
    }

    @PostMapping
    public TestTempDto create(@Valid @RequestBody TestTempDto dto) {
        TestTemp testTemp = testTempService.convertDtoToEntity(dto);
        testTempService.save(testTemp);
        return testTempService.convertEntityToDto(testTemp);
    }

    @PutMapping("/{id}")
    public TestTempDto update(@PathVariable("id") Long id, @Valid @RequestBody TestTempDto dto) {
        TestTemp testTemp = testTempService.convertDtoToEntity(dto);
        Optional<TestTemp> oldData = testTempService.findById(id);
        if (oldData.isEmpty()) {
            throw this.responseErrorNotfound();
        }
        testTempService.update(testTemp);
        return testTempService.convertEntityToDto(testTemp);
    }

    @GetMapping("/{id}")
    public TestTempDto findOne(@PathVariable("id") Long id) {
        Optional<TestTemp> testTemp = testTempService.findById(id);
        if (testTemp.isEmpty()) {
            throw this.responseErrorNotfound();
        }
        return testTempService.convertEntityToDto(testTemp.get());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable("id") Long id) {
        Optional<TestTemp> testTemp = testTempService.findById(id);
        if (testTemp.isEmpty()) {
            throw this.responseErrorNotfound();
        }
        testTempService.delete(testTemp.get());
        return this.responseDeleteMessage();
    }
}