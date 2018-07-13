package org.bahmni.mart.config.stepconfigurer;

import org.bahmni.mart.config.job.model.JobDefinition;
import org.bahmni.mart.exports.MetaDataExportStep;
import org.bahmni.mart.table.TableDataExtractor;
import org.bahmni.mart.table.TableGeneratorStep;
import org.bahmni.mart.table.domain.TableData;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.FlowJobBuilder;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import java.util.Arrays;

import static org.bahmni.mart.BatchUtils.constructSqlWithParameter;
import static org.bahmni.mart.BatchUtils.convertResourceOutputToString;

@Component
public class MetaDataStepConfigurer implements StepConfigurerContract {

    private static final String LIMIT = "LIMIT 1";

    @Autowired
    private ObjectFactory<MetaDataExportStep> metaDataExportStepObjectFactory;

    @Value("classpath:sql/metaDataCodeDictionary.sql")
    private Resource metaDataSqlResource;

    @Qualifier("openmrsJdbcTemplate")
    @Autowired
    private JdbcTemplate openmrsJDBCTemplate;

    @Autowired
    private TableGeneratorStep tableGeneratorStep;

    private TableData tableData;

    @Override
    public void generateTableData(JobDefinition jobDefinition) {
        String sql = convertResourceOutputToString(metaDataSqlResource);
        ResultSetExtractor<TableData> resultSetExtractor = new TableDataExtractor();
        sql = constructSqlWithParameter(sql,"conceptReferenceSource",
                jobDefinition.getConceptReferenceSource());
        tableData = openmrsJDBCTemplate.query(sql + LIMIT, resultSetExtractor);
        tableData.setName("meta_data_dictionary");
    }

    @Override
    public void createTables(JobDefinition jobDefinition) {
        tableGeneratorStep.createTables(Arrays.asList(tableData), jobDefinition);
    }

    @Override
    public void registerSteps(FlowBuilder<FlowJobBuilder> completeDataExport, JobDefinition jobDefinition) {
        MetaDataExportStep metaDataExportStep = metaDataExportStepObjectFactory.getObject();
        metaDataExportStep.setJobDefinition(jobDefinition);
        metaDataExportStep.setTableData(tableData);
        completeDataExport.next(metaDataExportStep.getStep());
    }

}
