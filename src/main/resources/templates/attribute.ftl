<@compress single_line=true>
SELECT
    <#list input.tableData.columns as column>
        <#if column.name?contains('_id')>
            ${column.name}
            <#assign primary_key = column.name>
        <#else >
        MAX(if( name =  '${column.name}',
                    if  (value_table.${input.eavJobData.valueColumnName} REGEXP '^[[:digit:]]*$' AND
                        ${getConceptName(input.eavJobData.valueColumnName)} IS NOT NULL,
                        ${getConceptName(input.eavJobData.valueColumnName)},
                        value_table.${input.eavJobData.valueColumnName})
        , NULL)) AS '${column.name}'
        </#if>
        <#if input.tableData.columns?seq_index_of(column) <=  input.tableData.columns?size - 2 >,</#if>
    </#list>
FROM ${input.eavJobData.attributeTableName} as value_table INNER JOIN  ${input.eavJobData.attributeTypeTableName} as type_table
WHERE value_table.${input.eavJobData.valueTableJoiningId} = type_table.${input.eavJobData.typeTableJoiningId}
GROUP BY ${primary_key} ;
</@compress>

<#function getConceptName conceptId>
    <#assign conceptName = "(select max(name) from concept_name where concept_id = value_table.${conceptId})">
    <#return conceptName>
</#function>


