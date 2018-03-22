package org.bahmni.mart.exports;

import org.bahmni.mart.config.job.JobDefinition;
import org.bahmni.mart.config.job.JobDefinitionUtil;
import org.bahmni.mart.table.listener.TableGeneratorJobListener;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.batch.core.Job;

import static org.bahmni.mart.CommonTestHelper.setValuesForMemberFields;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest(JobDefinitionUtil.class)
public class SimpleJobTemplateTest {

    @Mock
    private TableGeneratorJobListener listener;

    @Mock
    private Job job;

    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(JobDefinitionUtil.class);
    }

    @Test
    public void shouldBuildJobDependsOnJobConfiguration() throws Exception {
        SimpleJobTemplate simpleJobTemplate = new SimpleJobTemplate();
        setValuesForMemberFields(simpleJobTemplate, "tableGeneratorJobListener", listener);

        SimpleJobTemplate spyJobTemplate = spy(simpleJobTemplate);

        JobDefinition jobDefinition = new JobDefinition();
        String testJobName = "testJob";
        jobDefinition.setName(testJobName);
        jobDefinition.setChunkSizeToRead(100);
        String readerSql = "select * from table";
        jobDefinition.setReaderSql(readerSql);

        when(JobDefinitionUtil.getReaderSQLByIgnoringColumns(any(), anyString())).thenReturn(readerSql);
        doReturn(job).when((JobTemplate) spyJobTemplate).buildJob(jobDefinition, listener, readerSql);

        spyJobTemplate.buildJob(jobDefinition);
        verify(spyJobTemplate, times(1)).buildJob(jobDefinition, listener, readerSql);
        verifyStatic(times(1));
        JobDefinitionUtil.getReaderSQLByIgnoringColumns(null, readerSql);
    }
}