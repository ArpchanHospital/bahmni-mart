package org.bahmni.mart.config.job;

import org.bahmni.mart.BatchUtils;
import org.bahmni.mart.config.jsql.SqlParser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.internal.verification.VerificationModeFactory;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.core.io.Resource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.bahmni.mart.config.job.JobDefinitionUtil.getIgnoreConceptNamesForJob;
import static org.bahmni.mart.config.job.JobDefinitionUtil.getJobDefinitionByType;
import static org.bahmni.mart.config.job.JobDefinitionUtil.getReaderSQLByIgnoringColumns;
import static org.bahmni.mart.config.job.JobDefinitionUtil.getSeparateTableNamesForJob;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@PrepareForTest({SQLFileLoader.class, BatchUtils.class, SqlParser.class})
@RunWith(PowerMockRunner.class)
public class JobDefinitionUtilTest {

    @Mock
    private JobDefinition jobDefinition1;

    @Mock
    private JobDefinition jobDefinition2;

    @Test
    public void shouldReturnSameReaderSQLWhenIgnoreColumnsAreNull() {
        String expectedReaderSQL = "SELECT patient_program_id, program_id, patient_id, date_enrolled AS `enrolled_on`" +
                "FROM patient_program ";

        assertEquals(expectedReaderSQL, getReaderSQLByIgnoringColumns(null, expectedReaderSQL));
    }

    @Test
    public void shouldReturnSameReaderSQLWhenIgnoreColumnsAreEmpty() {
        String expectedReaderSQL = "SELECT patient_program_id, program_id, patient_id, date_enrolled AS `enrolled_on`" +
                "FROM patient_program ";

        assertEquals(expectedReaderSQL, getReaderSQLByIgnoringColumns(new ArrayList<>(), expectedReaderSQL));
    }

    @Test
    public void shouldReturnReaderSqlByFilteringIgnoredColumns() {
        String readerSQL = "SELECT patient_program_id, program_id AS `programId`, p.patient_id, " +
                "p.date_enrolled AS `enrolled_on`" +
                "FROM patient_program p";
        String expectedReaderSQL = "SELECT patient_program_id FROM patient_program p";

        List<String> ignoreColumns = Arrays.asList("patient_id", "enrolled_on", "programId");

        mockStatic(SqlParser.class);
        when(SqlParser.getUpdatedReaderSql(ignoreColumns, readerSQL)).thenReturn(expectedReaderSQL);
        assertEquals(expectedReaderSQL, getReaderSQLByIgnoringColumns(ignoreColumns, readerSQL));
    }

    @Test
    public void shouldReturnEmptySQLIfAllTheColumnsAreIgnored() {
        String readerSQL = "SELECT patient_program_id, program_id AS `programId`, p.patient_id, " +
                "p.date_enrolled AS `enrolled_on`";
        List<String> ignoreColumns = Arrays.asList("patient_id", "enrolled_on", "programId", "patient_program_id");

        mockStatic(SqlParser.class);
        when(SqlParser.getUpdatedReaderSql(ignoreColumns, readerSQL)).thenReturn("");
        assertEquals("", getReaderSQLByIgnoringColumns(ignoreColumns, readerSQL));
    }

    @Test
    public void shouldReturnNullIfReaderSqlIsNull() {
        assertNull(getReaderSQLByIgnoringColumns(Collections.emptyList(), null));
    }

    @Test
    public void shouldReturnEmptySqlIfReaderSqlIsEmpty() {
        assertEquals("", getReaderSQLByIgnoringColumns(Collections.emptyList(), ""));
    }

    @Test
    public void shouldReturnReaderSqlWhenItIsNotEmpty() {
        JobDefinition jobDefinition = mock(JobDefinition.class);
        String expectedSQL = "SELECT * FROM table";
        when(jobDefinition.getReaderSql()).thenReturn(expectedSQL);

        String actualSql = JobDefinitionUtil.getReaderSQL(jobDefinition);

        assertEquals(expectedSQL, actualSql);
    }

    @Test
    public void shouldReturnSqlFromFileWhenReaderSqlIsEmpty() {
        JobDefinition jobDefinition = mock(JobDefinition.class);
        when(jobDefinition.getReaderSql()).thenReturn("");
        String filePath = "some path";
        when(jobDefinition.getSourceFilePath()).thenReturn(filePath);
        String expectedSql = "SELECT * FROM table";
        Resource resource = mock(Resource.class);
        mockStatic(SQLFileLoader.class);
        mockStatic(BatchUtils.class);
        when(SQLFileLoader.loadResource(filePath)).thenReturn(resource);
        when(BatchUtils.convertResourceOutputToString(resource)).thenReturn(expectedSql);

        String actualSql = JobDefinitionUtil.getReaderSQL(jobDefinition);

        assertEquals(expectedSql, actualSql);
        PowerMockito.verifyStatic(VerificationModeFactory.times(1));
        BatchUtils.convertResourceOutputToString(resource);
        PowerMockito.verifyStatic(VerificationModeFactory.times(1));
        SQLFileLoader.loadResource(filePath);
    }

    @Test
    public void shouldReturnAllIgnoreConceptNames() {
        List<String> ignoreColumnsConfig = Arrays.asList("concept_1", "concept_2");

        when(jobDefinition2.getType()).thenReturn("obs");
        when(jobDefinition2.getColumnsToIgnore()).thenReturn(ignoreColumnsConfig);

        List<String> ignoreConcepts = getIgnoreConceptNamesForJob(jobDefinition2);
        assertEquals(2, ignoreConcepts.size());
        assertTrue(ignoreColumnsConfig.containsAll(ignoreConcepts));
        verify(jobDefinition2, times(1)).getColumnsToIgnore();
    }

    @Test
    public void shouldReturnEmptyListAsIgnoreConceptNamesIfConfigIsNotPresent() {
        when(jobDefinition2.getType()).thenReturn("obs");
        when(jobDefinition2.getColumnsToIgnore()).thenReturn(null);

        List<String> ignoreConcepts = getIgnoreConceptNamesForJob(jobDefinition2);
        assertTrue(ignoreConcepts.isEmpty());
        verify(jobDefinition2, times(1)).getColumnsToIgnore();
    }

    @Test
    public void shouldFindObsJob() {
        when(jobDefinition1.getType()).thenReturn("eav");
        when(jobDefinition2.getType()).thenReturn("obs");

        JobDefinition obsJobDefinition = getJobDefinitionByType(Arrays.asList(jobDefinition1, jobDefinition2), "obs");

        verify(jobDefinition1, times(1)).getType();
        verify(jobDefinition2, times(1)).getType();
        assertEquals(jobDefinition2, obsJobDefinition);

    }

    @Test
    public void shouldGiveEmptyObsJobIfObsJobConfigIsNotPresent() throws Exception {
        when(jobDefinition1.getType()).thenReturn("eav");
        whenNew(JobDefinition.class).withNoArguments().thenReturn(jobDefinition2);

        assertTrue(getJobDefinitionByType(Arrays.asList(jobDefinition1), "obs") instanceof JobDefinition);
        verify(jobDefinition1, times(1)).getType();
    }

    @Test
    public void shouldReturnSeparateTableList() {
        JobDefinition jobDefinition = mock(JobDefinition.class);
        List<String> seperateTables = Arrays.asList("table1", "table2");
        when(jobDefinition.getSeparateTables()).thenReturn(seperateTables);

        List<String> expected = getSeparateTableNamesForJob(jobDefinition);

        assertNotNull(expected);
        assertEquals(2, expected.size());
        assertEquals(seperateTables.get(0), expected.get(0));
        assertEquals(seperateTables.get(1), expected.get(1));
    }

    @Test
    public void shouldGiveEmptyListAsSeparateTableNamesForAJobIfConfigIsNotPresent() {
        when(jobDefinition2.getType()).thenReturn("obs");
        when(jobDefinition2.getSeparateTables()).thenReturn(null);

        List<String> separateTableNames = getSeparateTableNamesForJob(jobDefinition2);

        assertTrue(separateTableNames.isEmpty());
        verify(jobDefinition2, times(1)).getSeparateTables();
    }

}