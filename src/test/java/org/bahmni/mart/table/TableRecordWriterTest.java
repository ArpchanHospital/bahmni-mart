package org.bahmni.mart.table;

import org.bahmni.mart.BatchUtils;
import org.bahmni.mart.helper.FreeMarkerEvaluator;
import org.bahmni.mart.table.domain.TableData;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.bahmni.mart.CommonTestHelper.setValuesForMemberFields;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
public class TableRecordWriterTest {

    @Mock
    private JdbcTemplate martJdbcTemplate;

    @Mock
    private FreeMarkerEvaluator<TableRecordHolder> tableRecordHolderFreeMarkerEvaluator;

    private TableRecordWriter tableRecordWriter;
    private Map<String, Object> items;

    @Before
    public void setUp() throws Exception {
        mockStatic(BatchUtils.class);
        tableRecordWriter = new TableRecordWriter();
        items = new HashMap<String, Object>() {
            {
                put("program_id", 123);
            }
        };
        TableData tableData = new TableData();
        tableData.setName("program");
        tableRecordWriter.setTableData(tableData);
        setValuesForMemberFields(tableRecordWriter, "martJdbcTemplate", martJdbcTemplate);
        setValuesForMemberFields(tableRecordWriter, "tableRecordHolderFreeMarkerEvaluator",
                tableRecordHolderFreeMarkerEvaluator);
    }

    @Test
    public void shouldExecuteEvaluatedInsertSql() throws Exception {
        String sql = "";
        when(tableRecordHolderFreeMarkerEvaluator.evaluate(anyString(), any(TableRecordHolder.class))).thenReturn(sql);

        tableRecordWriter.write(Arrays.asList(items));

        verify(martJdbcTemplate, times(1)).execute(sql);
        verify(tableRecordHolderFreeMarkerEvaluator, times(1)).evaluate(anyString(), any(TableRecordHolder.class));
    }
}