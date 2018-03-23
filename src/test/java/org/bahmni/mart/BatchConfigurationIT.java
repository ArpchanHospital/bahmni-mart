package org.bahmni.mart;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BatchConfigurationIT extends AbstractBaseBatchIT {
    @Autowired
    BatchConfiguration batchConfiguration;

    private Map<String, String> expectedPatientList;

    @Before
    public void setUp() {
        expectedPatientList = new HashMap<>();
        expectedPatientList.put("124", "Test");
        expectedPatientList.put("125", "Unknown");
        expectedPatientList.put("126", "Unknown");
        expectedPatientList.put("127", "Unknown");
        expectedPatientList.put("128", "Unknown");
        expectedPatientList.put("129", "Test 1");
        expectedPatientList.put("130", "Unknown");
        expectedPatientList.put("131", "Unknown");
        expectedPatientList.put("132", "Unknown");
        expectedPatientList.put("133", "Unknown");
    }

    @Test
    @Sql(scripts = "classpath:testDataSet/insertPatientsData.sql")
    public void shouldCreateTablesBasedOnJobConfiguration() {

        batchConfiguration.run();

        List<Map<String, Object>> tables = martJdbcTemplate.queryForList("SELECT TABLE_NAME FROM " +
                "INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA='PUBLIC' AND TABLE_NAME <> 'test'");
        assertTrue(tables.size() >= 6);

        List<String> tableNames = tables.stream().map(table -> table.get("TABLE_NAME").toString())
                .collect(Collectors.toList());
        List<String> expectedTableNames = Arrays.asList("patient_allergy_status_test", "first_stage_validation",
                "fstg,_specialty_determined_by_mlo", "follow-up_validation", "stage", "person_attributes");
        assertTrue(tableNames.containsAll(expectedTableNames));
        verifyTableColumns();

        List<Map<String, Object>> patientList = martJdbcTemplate
                .queryForList("SELECT * FROM \"patient_allergy_status_test\"");
        assertEquals(10, patientList.size());

        for (Map<String, Object> row : patientList) {
            String patientId = row.get("patient_id").toString();
            String allergyStatus = row.get("allergy_status").toString();
            assertEquals(expectedPatientList.get(patientId), allergyStatus);
        }

    }

    @Test
    @Sql(scripts = "classpath:testDataSet/insertPatientsDataForIgnoreColumns.sql")
    public void shouldCreateTablesBasedOnJobConfigurationByIgnoringColumns() {

        batchConfiguration.run();

        List<Object> tableDataColumns = martJdbcTemplate.queryForList("SELECT column_name FROM " +
                "INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'patient_allergy_status_test1'" +
                " AND TABLE_SCHEMA='PUBLIC';")
                .stream().map(columns -> columns.get("COLUMN_NAME")).collect(Collectors.toList());
        List<Map<String, Object>> patientList = martJdbcTemplate
                .queryForList("SELECT * FROM \"patient_allergy_status_test1\"");
        List<String> columnsName = tableDataColumns.stream().map(name -> name.toString().toLowerCase())
                .collect(Collectors.toList());

        assertEquals(10, patientList.size());
        assertEquals(2, tableDataColumns.size());
        assertTrue(columnsName.containsAll(Arrays.asList("patient_id", "allergy_status")));

        for (Map<String, Object> row : patientList) {
            String patientId = row.get("patient_id").toString();
            String allergyStatus = row.get("allergy_status").toString();
            assertEquals(expectedPatientList.get(patientId), allergyStatus);
        }
    }

    private void verifyTableColumns() {
        HashMap<String, List<String>> tableMap = new HashMap<>();
        tableMap.put("patient_allergy_status_test", Arrays.asList("patient_id", "allergy_status"));
        tableMap.put("first_stage_validation",
                Arrays.asList("id_first_stage_validation", "patient_id", "encounter_id"));
        tableMap.put("fstg,_specialty_determined_by_mlo",
                Arrays.asList("id_fstg,_specialty_determined_by_mlo", "patient_id", "encounter_id",
                        "id_first_stage_validation", "fstg,_specialty_determined_by_mlo"));
        tableMap.put("follow-up_validation", Arrays.asList("id_follow-up_validation", "patient_id", "encounter_id"));
        tableMap.put("stage", Arrays.asList("id_stage", "patient_id", "encounter_id", "id_first_stage_validation",
                "stage", "id_follow-up_validation"));
        tableMap.put("person_attributes", Arrays.asList("person_id", "givennamelocal", "familynamelocal",
                "middlenamelocal", "viber", "phonenumber2"));

        for (String tableName : tableMap.keySet()) {
            String sql = String.format("SELECT column_name FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = '%s' " +
                    "AND TABLE_SCHEMA='PUBLIC';", tableName);
            List<String> columnNames = martJdbcTemplate.queryForList(sql).stream()
                    .map(columns -> columns.get("COLUMN_NAME").toString().toLowerCase()).collect(Collectors.toList());

            assertEquals(tableMap.get(tableName).size(), columnNames.size());
            assertTrue(tableMap.get(tableName).containsAll(columnNames));
        }
    }
}