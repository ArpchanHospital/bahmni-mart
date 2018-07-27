package org.bahmni.mart.exports.template;

import org.bahmni.mart.config.job.model.CodeConfig;
import org.bahmni.mart.config.job.model.JobDefinition;
import org.bahmni.mart.table.CodesProcessor;
import org.bahmni.mart.table.listener.TableGeneratorJobListener;
import org.springframework.batch.core.Job;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.bahmni.mart.config.job.JobDefinitionUtil.getReaderSQL;
import static org.bahmni.mart.config.job.JobDefinitionUtil.getReaderSQLByIgnoringColumns;
import static org.bahmni.mart.config.job.JobDefinitionValidator.isValid;

@Component
@Scope(value = "prototype")
public class SimpleJobTemplate extends JobTemplate {

    @Autowired
    private TableGeneratorJobListener tableGeneratorJobListener;

    @Autowired
    private CodesProcessor codesProcessor;

    public Job buildJob(JobDefinition jobConfiguration) {
        incrementalStrategyContext.getStrategy(jobConfiguration.getType()).setListener(tableGeneratorJobListener);
        String readerSql = getReaderSql(jobConfiguration);
        List<CodeConfig> codeConfigs = jobConfiguration.getCodeConfigs();
        if (isValid(codeConfigs)) {
            codesProcessor.setCodeConfigs(codeConfigs);
            tableGeneratorJobListener.setCodesProcessor(codesProcessor);
            setPreProcessor(codesProcessor);
        }
        return buildJob(jobConfiguration, tableGeneratorJobListener, readerSql);
    }

    private String getReaderSql(JobDefinition jobDefinition) {
        List<String> columnsToIgnore = jobDefinition.getColumnsToIgnore();
        String readerSQL = getReaderSQLByIgnoringColumns(columnsToIgnore, getReaderSQL(jobDefinition));
        return getUpdatedReaderSql(jobDefinition, readerSQL);
    }
}
