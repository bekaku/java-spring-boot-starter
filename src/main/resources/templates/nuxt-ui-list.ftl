<#-- กำหนดตัวแปรและฟังก์ชันพื้นฐานที่ใช้บ่อย -->
<#assign excludedFields = ["createdDate", "createdUser", "updatedDate", "updatedUser", "deleted"]>
<#assign entityLower = entityName?uncap_first>
<#assign kebabTable = tableName?replace('_', '-')>

<script setup lang="ts">

/* move this interface to /app/types/models.ts
export interface ${entityName} extends Id {
<#list properties as prop>
  <#if !excludedFields?seq_contains(prop.propertyName)>
    <#-- หา TypeScript Type -->
    <#assign isObjLink = prop.propertyType?contains(".")>
    <#assign tsType = "undefined">
    <#if isObjLink>
      <#assign tsType = prop.propertyType?substring(prop.propertyType?last_index_of(".") + 1)>
    <#elseif prop.propertyType == "boolean">
      <#assign tsType = "boolean">
    <#elseif prop.propertyType == "string" || prop.propertyType == "LocalDate" || prop.propertyType == "LocalDateTime">
      <#assign tsType = "string">
    <#elseif ["float", "big_decimal", "integer"]?seq_contains(prop.propertyType)>
      <#assign tsType = "number">
    </#if>
    <#-- แสดงผลบรรทัด Interface -->
  ${prop.propertyName}<#if prop.nullable>?</#if>: ${tsType}<#if prop.nullable> | null</#if>
  </#if>
</#list>
}
*/

/* move this message object to /app/i18n/th/model.ts and /app/i18n/en/model.ts under model:{}
    ,"${entityLower}": {
      "table": "${entityLower}",
<#list properties as prop>
  <#if !excludedFields?seq_contains(prop.propertyName)>
      "${prop.propertyName}": "${prop.propertyName}",
  </#if>
</#list>
    }
*/

import type { TableColumn } from "@nuxt/ui";
import { ICrudListHeaderOptionSearchType, type ICrudFilterOptions} from "~/types/common";
import type { ${entityName} } from "~/types/models";

definePageMeta({
  pageName: 'model.${entityLower}.table',
  requiresPermission: ["${tableName}_list"],
});
const { t } = useLang();
const {formatDateTime, formatDate} = useDateFns();
const UIcon = resolveComponent("UIcon");

const {
  dataList,
  loading,
  firstLoaded,
  pages,
  sorts,
  onPageChange,
  onPerPageChange,
  onSort,
  onReload,
  onSearch,
  onItemDelete,
  onNewForm,
  onItemClick,
  onItemCopy,
  crudName,
  onKeywordSearch,
} = useCrudList<${entityName}>({
  crudName: "${entityName}",
  apiEndpoint: "/api/${entityLower}",
  headers: [],
  itemsPerPage: 10,
  defaultSorts: [
    {
      column: "id",
      mode: "desc",
    },
  ],
});
const columns = ref<TableColumn<${entityName}>[]>([
<#list properties as prop>
  <#if !excludedFields?seq_contains(prop.propertyName)>
    <#-- เตรียมตัวแปรเช็ค Type สำหรับสร้าง Column -->
    <#assign isNumber = ["float", "big_decimal", "integer"]?seq_contains(prop.propertyType)>
    <#assign isBoolean = (prop.propertyType == "boolean")>
    <#assign isDate = (prop.propertyType == "LocalDate")>
    <#assign isDatetime = (prop.propertyType == "LocalDateTime")>

    <#-- หากำหนด Search Type และ Operation -->
    <#assign searchType = "TEXT">
    <#assign searchOp = ":">
    <#if isBoolean>
      <#assign searchType = "BOOLEAN">
      <#assign searchOp = "=">
    <#elseif isDate>
      <#assign searchType = "DATE">
      <#assign searchOp = ">=">
    <#elseif isDatetime>
      <#assign searchType = "DATE_TIME">
      <#assign searchOp = ">=">
    <#elseif isNumber>
      <#assign searchType = "NUMBER">
      <#assign searchOp = ">=">
    </#if>

  {
    accessorKey: "${prop.propertyName}",
    header: t("model.${entityLower}.${prop.propertyName}"),
    <#if isBoolean>
    cell: ({ row }) => {
      const ${prop.propertyName} = row.getValue("${prop.propertyName}");
      return h(UIcon, {
        name: ${prop.propertyName} ? "lucide:circle-check" : "lucide:circle-x",
        class: ${prop.propertyName} ? "text-primary size-6" : "text-neutral size-6",
      });
    },
    <#elseif isDate || isDatetime>
    cell: ({ row }) => {
      const ${prop.propertyName} = row.getValue("${prop.propertyName}") as string;
      return ${isDatetime?string("formatDateTime", "formatDate")}({
        date: ${prop.propertyName},
        iso:true
      });
    },
    <#elseif isNumber>
    cell: ({ row }) => {
      const ${prop.propertyName} = row.getValue("${prop.propertyName}") as string;
      numberFormat(${prop.propertyName})
    },
    <#else>
    cell: ({ row }) => row.getValue("${prop.propertyName}"),
    </#if>
    meta: {
      options: {
        sortable: true,
        searchable: true,
        searchType: ICrudListHeaderOptionSearchType.${searchType},
        searchOperation: "${searchOp}",
        searchModel: ${isBoolean?string("true", "\"\"")},
        searchOperationReadonly: ${isBoolean?string("true", "false")},
      } as ICrudFilterOptions,
    } as any,
  },
  </#if>
</#list>
]);

const onCellTypeClick = (index: number) => {
  let rowItem = dataList.value[index];
  console.log("rowItem", rowItem);
};
</script>

<template>
  <BaseDashboardPanel id="${kebabTable}-index" :title="$t('model.${entityLower}.table')">
    <BaseTable
      icon="lucide:layout-list"
      :title="$t('model.${entityLower}.table')"
      :crud-name="crudName"
      :list="dataList"
      :show-checkbox="true"
      :loading="loading"
      :first-loaded="firstLoaded"
      :columns="columns"
      v-model:sorts="sorts"
      v-model:paging="pages"
      @on-item-delete="onItemDelete"
      @on-page-no-change="onPageChange"
      @on-items-perpage-change="onPerPageChange"
      @on-new-form="onNewForm"
      @on-item-click="onItemClick"
      @on-item-copy="onItemCopy"
      @on-sort="onSort"
      @on-reload="onReload"
      @on-keyword-search="onKeywordSearch"
      @on-search="onSearch"
    >
      <!-- accessorKey or id of column can be used as slots everywhere in side BaseTable
        <template #actions-cell="{ row }">
         Action slot
        </template>
    <#list properties as prop>
      <#if !excludedFields?seq_contains(prop.propertyName)>
        <template #${prop.propertyName}-cell="{ row }">
         ${prop.propertyName} slot
        </template>
      </#if>
    </#list>
    -->
    </BaseTable>
  </BaseDashboardPanel>
</template>