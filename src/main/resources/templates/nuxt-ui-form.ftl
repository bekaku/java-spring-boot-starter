<#-- กำหนดตัวแปรและฟังก์ชันพื้นฐานที่ใช้บ่อย -->
<#assign excludedFields = ["createdDate", "createdUser", "updatedDate", "updatedUser", "deleted"]>
<#assign entityLower = entityName?uncap_first>
<#assign kebabTable = tableName?replace('_', '-')>

<script setup lang="ts">
import z from "zod";
import type { ${entityName} } from "~/types/models";

definePageMeta({
  pageName: "model.${entityLower}.table",
  requiresPermission: ["${tableName}_view", "${tableName}_add", "${tableName}_edit"],
});
const { t } = useLang();

const schema = z.object({
<#list properties as prop>
  <#if !excludedFields?seq_contains(prop.propertyName)>
    <#assign isNumber = ["float", "big_decimal", "integer"]?seq_contains(prop.propertyType)>
    <#assign isBoolean = (prop.propertyType == "boolean")>
    <#assign isDate = (prop.propertyType == "LocalDate")>
    <#assign isDatetime = (prop.propertyType == "LocalDateTime")>
    <#assign isRefClass = prop.propertyType?contains(".")>
    <#assign isTextArea = prop.typeTextArea>

    <#if !isRefClass && !isDatetime>
      <#assign typeString = "text">
      <#assign zString = "string()">
      <#if isNumber>
        <#assign typeString = "number-step">
        <#assign zString = "number()">
      <#elseif isTextArea>
        <#assign typeString = "textarea">
      <#elseif isBoolean>
        <#assign typeString = "checkbox">
        <#assign zString = "any()">
      <#elseif isDate>
        <#assign typeString = "date">
      </#if>
  ${prop.propertyName}: z
    .${zString}
    <#if !prop.nullable>
    .min(1, t("error.validateRequireField"))
    </#if>
    .describe(
      uiConfig({
        label: t("model.${entityLower}.${prop.propertyName}"),
        ui: {
          type: "${typeString}",
          required: ${(!prop.nullable)?c},
          clearable: true,
          <#if isDate>
          numberOfMonths: 2,
          </#if>
          <#if (prop.length?? && prop.length > 0)>
          maxlength: ${prop.length?c},
          </#if>
        },
      }),
    )<#if prop.nullable>.optional(),<#else>,</#if>
    <#elseif isDatetime>
  //implement datetime
  //${prop.propertyName}
    <#else>
  //implement ref object
  //${prop.propertyName}
    </#if>
  </#if>
</#list>
});

type Schema = z.output<typeof schema>;

const state = ref<Partial<Schema>>({
<#list properties as prop>
  <#if !excludedFields?seq_contains(prop.propertyName)>
    <#assign isNumber = ["float", "big_decimal", "integer"]?seq_contains(prop.propertyType)>
    <#assign isBoolean = (prop.propertyType == "boolean")>
    <#assign isRefClass = prop.propertyType?contains(".")>

    <#assign defaultVal = '""'>
    <#if isNumber>
      <#assign defaultVal = '0'>
    <#elseif isBoolean>
      <#assign defaultVal = 'true'>
    <#elseif isRefClass>
      <#assign defaultVal = 'null'>
    <#elseif prop.propertyType == "LocalDate">
      <#assign defaultVal = 'getCurrentDateByFormat()'>
    </#if>
  ${prop.propertyName}: ${defaultVal},
  </#if>
</#list>
});

const {
  crudAction,
  loading,
  crudName,
  isEditMode,
  onDelete,
  onBack,
  onEnableEditForm,
  onSubmit,
} = useCrudForm<${entityName}>(
  {
    crudName: "${entityName}",
  },
  state,
);
</script>

<template>
  <BaseDashboardPanel
    id="${kebabTable}-crud-index"
    :title="$t('model.${entityLower}.table')"
  >
    <BaseForm
      :zod-schema="schema"
      v-model="state"
      :edit-mode="isEditMode"
      :crud-action="crudAction"
      :loading="loading"
      :crud-name="crudName"
      icon="lucide:form"
      :title="$t('model.${entityLower}.table')"
      orientation="horizontal"
      class="max-w-[1020px]"
      @on-back="onBack"
      @on-edit-enable="onEnableEditForm"
      @on-submit="onSubmit"
      @on-delete="onDelete"
    >
      <!-- you can override prepend fields here -->
      <!-- <template #prepend-fields> </template> -->

      <!-- you can override form fields here auto generate slot by field-<propertyName> -->
      <!--
<#list properties as prop>
  <#if !excludedFields?seq_contains(prop.propertyName)>
      <template #field-${prop.propertyName}>
        <UFormField :label="$t('model.${entityLower}.${prop.propertyName}')" name="${prop.propertyName}" class="w-full">
          Override ${prop.propertyName}
        </UFormField>
      </template>
  </#if>
</#list>
      -->
    </BaseForm>
  </BaseDashboardPanel>
</template>