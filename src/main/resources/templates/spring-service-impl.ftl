<#assign entityLower = entityName?uncap_first>
<#assign dtoType = haveDto?string(entityName + "Dto", entityName)>
package ${rootPackage}.serviceImpl;

import ${rootPackage}.dto.ResponseListDto;
<#if haveDto>
import ${rootPackage}.dto.${entityName}Dto;
import ${rootPackage}.mapper.${entityName}Mapper;
</#if>
import ${rootPackage}.model.${entityName};
import ${rootPackage}.repository.${entityName}Repository;
import ${rootPackage}.service.${entityName}Service;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import com.bekaku.api.spring.specification.SearchSpecification;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
<#if haveDto>
import java.util.stream.Collectors;
</#if>

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ${entityName}ServiceImpl implements ${entityName}Service {

    private final ${entityName}Repository ${entityLower}Repository;
<#if haveDto>
    private final ${entityName}Mapper modelMapper;
</#if>

    @Override
    public ResponseListDto<${dtoType}> findAllWithPaging(Pageable pageable) {
        Page<${entityName}> result = ${entityLower}Repository.findAll(pageable);
        return getListFromResult(result);
    }

    @Override
    public ResponseListDto<${dtoType}> findAllWithSearch(SearchSpecification<${entityName}> specification, Pageable pageable) {
        return getListFromResult(findAllPageSearchSpecificationBy(specification, pageable));
    }

    @Override
    public ResponseListDto<${dtoType}> findAllBy(Specification<${entityName}> specification, Pageable pageable) {
        return getListFromResult(findAllPageSpecificationBy(specification, pageable));
    }

    @Override
    public Page<${entityName}> findAllPageSpecificationBy(Specification<${entityName}> specification, Pageable pageable) {
        return ${entityLower}Repository.findAll(specification, pageable);
    }

    @Override
    public Page<${entityName}> findAllPageSearchSpecificationBy(SearchSpecification<${entityName}> specification, Pageable pageable) {
        return ${entityLower}Repository.findAll(specification, pageable);
    }

    private ResponseListDto<${dtoType}> getListFromResult(Page<${entityName}> result) {
        return new ResponseListDto<>(result.getContent()<#if haveDto>
                .stream()
                .map(this::convertEntityToDto)
                .collect(Collectors.toList())</#if>
                , result.getTotalPages(), result.getTotalElements(), result.isLast());
    }

    @Override
    public List<${entityName}> findAll() {
        return ${entityLower}Repository.findAll();
    }

    @Transactional
    @Override
    public ${entityName} save(${entityName} ${entityLower}) {
        return ${entityLower}Repository.save(${entityLower});
    }

    @Transactional
    @Override
    public ${entityName} update(${entityName} ${entityLower}) {
        return ${entityLower}Repository.save(${entityLower});
    }

    @Override
    public Optional<${entityName}> findById(Long id) {
        return ${entityLower}Repository.findById(id);
    }

    @Transactional
    @Override
    public void delete(${entityName} ${entityLower}) {
        ${entityLower}Repository.delete(${entityLower});
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        ${entityLower}Repository.deleteById(id);
    }

    @Override
    public ${dtoType} convertEntityToDto(${entityName} ${entityLower}) {
<#if haveDto>
        return modelMapper.toDto(${entityLower});
<#else>
        return ${entityLower};
</#if>
    }

    @Override
    public ${entityName} convertDtoToEntity(${dtoType} <#if haveDto>${entityLower}Dto<#else>${entityLower}</#if>) {
<#if haveDto>
        return modelMapper.toEntity(${entityLower}Dto);
<#else>
        return ${entityLower};
</#if>
    }
}