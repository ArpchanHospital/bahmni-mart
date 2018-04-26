package org.bahmni.mart.config.view;

import org.apache.commons.lang3.StringUtils;
import org.bahmni.mart.helper.RspConfigHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static org.bahmni.mart.table.FormTableMetadataGenerator.addPrefixToName;
import static org.bahmni.mart.table.FormTableMetadataGenerator.getProcessedName;

@Component
public class RspViewDefinition {

    private static final String RSP = "Rsp";

    @Autowired
    private RspConfigHelper rspConfigHelper;

    @Qualifier("martNamedJdbcTemplate")
    @Autowired
    protected NamedParameterJdbcTemplate martNamedJdbcTemplate;

    public ViewDefinition getDefinition() {
        ViewDefinition viewDefinition = new ViewDefinition();
        viewDefinition.setName("registration_second_page_view");
        viewDefinition.setSql(getSql());

        return viewDefinition;
    }

    private String getSql() {
        List<String> rspConcepts = rspConfigHelper.getRspConcepts();
        List<String> tableNames = rspConcepts.stream()
                .map(conceptName -> getProcessedName(addPrefixToName(conceptName, RSP)))
                .collect(Collectors.toList());

        return tableNames.size() > 0 ? createSql(tableNames) : "";
    }

    private String createSql(List<String> tableNames) {
        String selectClause = getSelectClause(getTablesMetaData(tableNames));
        String sql = format("SELECT %s FROM %s", selectClause, tableNames.get(0));

        for (int index = 1; index < tableNames.size(); index++) {
            sql = sql.concat(format(" INNER JOIN %s ON %s", tableNames.get(index),
                    getJoining(tableNames.get(index - 1), tableNames.get(index))));
        }

        return sql;
    }

    private String getSelectClause(List<Map<String, Object>> tablesData) {
        List<String> columns = tablesData.stream().map(tableData ->
                format("%s.%s AS %s_%s", tableData.get("table_name"), tableData.get("column_name"),
                        tableData.get("table_name"), tableData.get("column_name"))
        ).collect(Collectors.toList());

        return StringUtils.join(columns, ",");
    }

    private List<Map<String, Object>> getTablesMetaData(List<String> tableNames) {
        String sql = "SELECT column_name, table_name FROM " +
                "INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME in (:tableNames)" +
                " AND TABLE_SCHEMA='public';";
        Map<String, List<String>> params = new HashMap<>();
        params.put("tableNames", tableNames);

        return martNamedJdbcTemplate.queryForList(sql, params);
    }

    private String getJoining(String firstTable, String secondTable) {
        return format("%s.patient_id = %s.patient_id AND %s.encounter_id = %s.encounter_id",
                secondTable, firstTable, secondTable, firstTable);
    }
}
