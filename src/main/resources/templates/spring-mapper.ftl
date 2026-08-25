package ${rootPackage}.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import ${rootPackage}.dto.${entityName}Dto;
import ${rootPackage}.model.${entityName};

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ${entityName}Mapper {
    ${entityName}Dto toDto(${entityName} entity);
    ${entityName} toEntity(${entityName}Dto dto);
}