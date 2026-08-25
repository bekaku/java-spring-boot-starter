package ${rootPackage}.service;

<#if haveDto>
import ${rootPackage}.dto.${entityName}Dto;
</#if>
import ${rootPackage}.model.${entityName};

public interface ${entityName}Service extends BaseService<${entityName}, <#if haveDto>${entityName}Dto<#else>${entityName}</#if>> {
}