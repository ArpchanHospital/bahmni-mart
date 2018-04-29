package org.bahmni.mart.exports;

import org.bahmni.mart.BatchUtils;
import org.bahmni.mart.form.domain.Obs;
import org.bahmni.mart.table.SpecialCharacterResolver;
import org.bahmni.mart.table.domain.TableColumn;
import org.bahmni.mart.table.domain.TableData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.bahmni.mart.table.FormTableMetadataGenerator.getProcessedName;


public class ObsRecordExtractorForTable {

    private String tableName;

    private List<Map<String, String>> recordList = new ArrayList<>();

    public ObsRecordExtractorForTable(String tableName) {
        this.tableName = tableName;
    }

    public void execute(List<? extends List<Obs>> items, TableData tableData) {
        for (List<Obs> record : items) {
            if (!record.isEmpty()) {
                Map<String, String> recordMap = new HashMap<>();
                tableData.getColumns().forEach(tableColumn -> recordMap.put(tableColumn.getName(), null));

                mapRecordsWithColumns(tableData, record, recordMap);
                mapPatientAndEncounterIds(tableData, record, recordMap);

                recordList.add(recordMap);
            }
        }
    }

    private void mapRecordsWithColumns(TableData tableData, List<Obs> record, Map<String, String> recordMap) {
        record.forEach(obs -> tableData.getColumns().forEach(tableColumn -> {
            String tableColumnName = tableColumn.getName();
            String actualColumnName = SpecialCharacterResolver.getActualColumnName(tableData, tableColumn);
            if (actualColumnName.equals(getProcessedName(obs.getField().getName()))) {
                replace(recordMap, tableColumnName, obs.getValue(), tableColumn.getType());
            } else if (tableColumnName.contains("id_") && isNull(recordMap, tableColumnName)) {
                mapConstraints(recordMap, obs, tableColumn);
            }
        }));
    }

    private boolean isNull(Map<String, String> recordMap, String tableColumnName) {
        return recordMap.get(tableColumnName) == null;
    }

    private void mapConstraints(Map<String, String> recordMap, Obs obs, TableColumn tableColumn) {
        if (isForeignKey(obs, tableColumn)) {
            replace(recordMap, tableColumn.getName(), obs.getParentId().toString(), tableColumn.getType());
        } else if (tableColumn.getReference() == null && isConstraintName(tableName, tableColumn.getName())) {
            replace(recordMap, tableColumn.getName(), obs.getId().toString(), tableColumn.getType());
        }
    }

    private boolean isForeignKey(Obs obs, TableColumn tableColumn) {
        return tableColumn.getReference() != null && obs.getParentName() != null &&
                isConstraintName(obs.getParentName(), tableColumn.getName());
    }

    private void replace(Map<String, String> recordMap, String key, String value, String type) {
        recordMap.replace(key, BatchUtils.getPostgresCompatibleValue(value, type));
    }

    private boolean isConstraintName(String conceptName, String tableColumnName) {
        return tableColumnName.equals("id_" + getProcessedName(conceptName));
    }

    private void mapPatientAndEncounterIds(TableData tableData, List<Obs> record, Map<String, String> recordMap) {
        Obs obs = record.get(0);
        for (TableColumn tableColumn : tableData.getColumns()) {
            String tableColumnName = tableColumn.getName();
            if (isNull(recordMap, tableColumnName)) {
                replace(recordMap, tableColumnName, getValue(tableColumnName, obs), tableColumn.getType());
            }
        }
    }

    private String getValue(String columnName, Obs obs) {
        return "encounter_id".equals(columnName) ? obs.getEncounterId() :
                "patient_id".equals(columnName) ? obs.getPatientId() : null;
    }

    public List<Map<String, String>> getRecordList() {
        return recordList;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public void setRecordList(List<Map<String, String>> recordList) {
        this.recordList = recordList;
    }


}
