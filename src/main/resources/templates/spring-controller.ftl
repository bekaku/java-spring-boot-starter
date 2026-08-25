package ${rootPackage}.controller.api;

import ${rootPackage}.configuration.I18n;
import ${rootPackage}.dto.ResponseListDto;
<#if haveDto>
import ${rootPackage}.dto.${entityName}Dto;
</#if>
import ${rootPackage}.model.${entityName};
import ${rootPackage}.service.${entityName}Service;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import ${rootPackage}.specification.SearchSpecification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.data.domain.Pageable;
import com.bekaku.api.spring.util.ControllerUtil;
import jakarta.servlet.http.HttpServletRequest;
import ${rootPackage}.util.ConstantData;

import jakarta.validation.Valid;
import java.util.Optional;
import java.util.List;
import org.springframework.http.HttpStatus;

@Slf4j
@RequestMapping(path = "/api/${entityNameLower}")
@RestController
@RequiredArgsConstructor
public class ${entityName}Controller extends BaseApiController{

    private final ${entityName}Service ${entityNameLower}Service;
    private final I18n i18n;

    <#if havePermission>
    @PreAuthorize("@permissionChecker.hasPermission('${entityNameSnake}_list')")
    </#if>
    @GetMapping
    public ResponseEntity<ResponseListDto<${entityName}<#if haveDto>Dto</#if>>> findAll(HttpServletRequest request, Pageable pageable) {
        SearchSpecification<${entityName}> specification = ControllerUtil.buildSpecification(request, List.of());
        return this.responseEntity(${entityNameLower}Service.findAllWithSearch(specification, getPageable(pageable, ${entityName}.getSort())), HttpStatus.OK);
    }

    <#if havePermission>
    @PreAuthorize("@permissionChecker.hasPermission('${entityNameSnake}_add')")
    </#if>
    @PostMapping
    public ${entityName}<#if haveDto>Dto</#if> create(@Valid @RequestBody ${entityName}<#if haveDto>Dto dto<#else> ${entityNameLower}</#if>) {
        <#if haveDto>
        ${entityName} ${entityNameLower} = ${entityNameLower}Service.convertDtoToEntity(dto);
        </#if>
        ${entityNameLower}Service.save(${entityNameLower});
        <#if haveDto>
        return ${entityNameLower}Service.convertEntityToDto(${entityNameLower});
        <#else>
        return ${entityNameLower};
        </#if>
    }

    <#if havePermission>
    @PreAuthorize("@permissionChecker.hasPermission('${entityNameSnake}_edit')")
    </#if>
    @PutMapping("/{id}")
    public ${entityName}<#if haveDto>Dto</#if> update(@PathVariable("id") Long id, @Valid @RequestBody ${entityName}<#if haveDto>Dto dto<#else> ${entityNameLower}</#if>) {
        <#if haveDto>
        ${entityName} ${entityNameLower} = ${entityNameLower}Service.convertDtoToEntity(dto);
        </#if>
        Optional<${entityName}> oldData = ${entityNameLower}Service.findById(id);
        if (oldData.isEmpty()) {
            throw this.responseErrorNotfound();
        }
        ${entityNameLower}Service.update(${entityNameLower});
        <#if haveDto>
        return ${entityNameLower}Service.convertEntityToDto(${entityNameLower});
        <#else>
        return ${entityNameLower};
        </#if>
    }

    <#if havePermission>
    @PreAuthorize("@permissionChecker.hasPermission('${entityNameSnake}_view')")
    </#if>
    @GetMapping("/{id}")
    public ${entityName}<#if haveDto>Dto</#if> findOne(@PathVariable("id") Long id) {
        Optional<${entityName}> ${entityNameLower} = ${entityNameLower}Service.findById(id);
        if (${entityNameLower}.isEmpty()) {
            throw this.responseErrorNotfound();
        }
        <#if haveDto>
        return ${entityNameLower}Service.convertEntityToDto(${entityNameLower}.get());
        <#else>
        return ${entityNameLower}.get();
        </#if>
    }

    <#if havePermission>
    @PreAuthorize("@permissionChecker.hasPermission('${entityNameSnake}_delete')")
    </#if>
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable("id") Long id) {
        Optional<${entityName}> ${entityNameLower} = ${entityNameLower}Service.findById(id);
        if (${entityNameLower}.isEmpty()) {
            throw this.responseErrorNotfound();
        }
        ${entityNameLower}Service.delete(${entityNameLower}.get());
        return this.responseDeleteMessage();
    }
}