package org.bahmni.analytics.exports;

import org.bahmni.analytics.form.ObservationProcessor;
import org.bahmni.analytics.form.domain.BahmniForm;
import org.bahmni.analytics.form.domain.Obs;
import org.bahmni.analytics.helper.FreeMarkerEvaluator;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

import static org.bahmni.analytics.BatchUtils.stepNumber;

@Component
@Scope(value = "prototype")
public class ObservationExportStep {

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private FreeMarkerEvaluator<BahmniForm> freeMarkerEvaluator;

    private BahmniForm form;

    @Autowired
    private ObjectFactory<ObservationProcessor> observationProcessorFactory;

    @Autowired
    private ObjectFactory<DatabaseObsWriter> databaseObsWriterObjectFactory;


    public Step getStep() {
        return stepBuilderFactory.get(getStepName())
                .<Map<String, Object>, List<Obs>>chunk(100)
                .reader(obsReader())
                .processor(observationProcessor())
                .writer(getWriter())
                .build();
    }

    private JdbcCursorItemReader<Map<String, Object>> obsReader() {
        String sql = freeMarkerEvaluator.evaluate("obsWithParentSql.ftl", form);
        JdbcCursorItemReader<Map<String, Object>> reader = new JdbcCursorItemReader<>();
        reader.setDataSource(dataSource);
        reader.setSql(sql);
        reader.setRowMapper(new ColumnMapRowMapper());
        return reader;
    }

    private ObservationProcessor observationProcessor() {
        ObservationProcessor observationProcessor = observationProcessorFactory.getObject();
        observationProcessor.setForm(form);
        return observationProcessor;
    }

    private DatabaseObsWriter getWriter() {
        DatabaseObsWriter writer = databaseObsWriterObjectFactory.getObject();
        writer.setForm(this.form);
        return writer;
    }

    public void setForm(BahmniForm form) {
        this.form = form;
    }

    public String getStepName() {
        stepNumber++;
        String formName = String.format("Step-%d %s", stepNumber, form.getFormName().getName());
        return formName.substring(0, Math.min(formName.length(), 100));
    }
}
