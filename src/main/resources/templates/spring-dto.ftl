<#assign excludedFields = ["createdDate", "createdUser", "updatedDate", "updatedUser", "deleted"]>
package ${rootPackage}.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@Accessors(chain = true)
public class ${entityName}Dto extends DtoId {
<#list properties as prop>
  <#if !excludedFields?seq_contains(prop.propertyName)>
    <#assign isRefClass = prop.propertyType?contains(".")>
    <#assign javaType = "">
    <#if isRefClass>
      <#-- คลีนเอาเฉพาะชื่อ Class ตัดส่วนเกิน เช่น (..., @..., ,...) -->
      <#assign cleanType = prop.propertyType?split("(")[0]?split("@")[0]?split(",")[0]>
      <#assign javaType = cleanType?substring(cleanType?last_index_of(".") + 1)>
    <#elseif prop.propertyType == "boolean">
      <#assign javaType = "boolean">
    <#elseif prop.propertyType == "string" || prop.propertyType == "text">
      <#assign javaType = "String">
    <#elseif prop.propertyType == "LocalDate">
      <#assign javaType = "LocalDate">
    <#elseif prop.propertyType == "LocalDateTime">
      <#assign javaType = "LocalDateTime">
    <#elseif prop.propertyType == "float">
      <#assign javaType = "Float">
    <#elseif prop.propertyType == "big_decimal">
      <#assign javaType = "BigDecimal">
    <#elseif prop.propertyType == "integer">
      <#assign javaType = "int">
    <#elseif prop.propertyType == "long">
      <#assign javaType = "Long">
    </#if>
    <#if javaType != "">

      <#-- Prefix สำหรับ comment out เมื่อเป็น Class/Enum อ้างอิง -->
      <#assign commentPrefix = isRefClass?string("//", "")>
      <#if prop.propertyType == "LocalDate">
    ${commentPrefix}@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
      <#elseif prop.propertyType == "long">
    ${commentPrefix}@JsonFormat(shape = JsonFormat.Shape.STRING)
      </#if>
      <#if !prop.nullable>
    ${commentPrefix}@NotEmpty(message = "{error.NotEmpty}")
      </#if>
      <#if prop.length?? && prop.propertyType != "big_decimal" && prop.propertyType != "text" && prop.propertyType != "boolean">
        <#if prop.propertyType == "LocalDate">
    ${commentPrefix}@Size(max = 10, message = "{error.SizeLimitMaxFormat}")
        <#elseif prop.propertyType == "LocalDateTime">
    ${commentPrefix}@Size(max = 19, message = "{error.SizeLimitMaxFormat}")
        <#else>
    ${commentPrefix}@Size(max = ${prop.length?c}, message = "{error.SizeLimitMaxFormat}")
        </#if>
      <#elseif prop.propertyType == "big_decimal">
    ${commentPrefix}@DecimalMax(value = "999999999999.0", message = "{error.DecimalMax.message}")
    ${commentPrefix}@DecimalMin(value = "0.0", message = "{error.DecimalMin.message}")
      </#if>
      <#if isRefClass>
    //private ${javaType} ${prop.propertyName};
      <#else>
    private ${javaType} ${prop.propertyName};
      </#if>
    </#if>
  </#if>
</#list>
}