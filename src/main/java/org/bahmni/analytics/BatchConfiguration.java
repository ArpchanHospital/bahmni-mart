package org.bahmni.analytics;

import freemarker.template.TemplateExceptionHandler;
import org.apache.commons.io.FileUtils;
import org.bahmni.analytics.exception.BatchResourceException;
import org.bahmni.analytics.exports.AppointmentSchedulingExportStep;
import org.bahmni.analytics.exports.BedManagementExportStep;
import org.bahmni.analytics.exports.DrugOrderBaseExportStep;
import org.bahmni.analytics.exports.MetaDataCodeDictionaryExportStep;
import org.bahmni.analytics.exports.NonTBDrugOrderBaseExportStep;
import org.bahmni.analytics.exports.ObservationExportStep;
import org.bahmni.analytics.exports.OtExportStep;
import org.bahmni.analytics.exports.TreatmentRegistrationBaseExportStep;
import org.bahmni.analytics.form.FormListProcessor;
import org.bahmni.analytics.form.domain.BahmniForm;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.FlowJobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import javax.annotation.PreDestroy;
import java.io.File;
import java.io.IOException;
import java.util.List;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration extends DefaultBatchConfigurer {

    public static final String FULL_DATA_EXPORT_JOB_NAME = "ammanExports";

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private TreatmentRegistrationBaseExportStep treatmentRegistrationBaseExportStep;

    @Autowired
    private DrugOrderBaseExportStep drugOrderBaseExportStep;

    @Autowired
    private NonTBDrugOrderBaseExportStep nonTBDrugOrderBaseExportStep;

    @Autowired
    private FormListProcessor formListProcessor;

    @Autowired
    private ObjectFactory<ObservationExportStep> observationExportStepFactory;

    @Value("${templates}")
    private Resource freemarkerTemplateLocation;

    @Autowired
    public JobCompletionNotificationListener jobCompletionNotificationListener;

    @Autowired
    private MetaDataCodeDictionaryExportStep metaDataCodeDictionaryExportStep;

    @Autowired
    private BedManagementExportStep bedManagementExportStep;

    @Autowired
    private AppointmentSchedulingExportStep appointmentSchedulingExportStep;

    @Value("${zipFolder}")
    private Resource zipFolder;

    @Value("${bahmniConfigFolder}")
    private Resource bahmniConfigFolder;

    @Autowired
    private ReportGenerator reportGenerator;

    @Autowired
    private OtExportStep otExportStep;

    private static final String DEFAULT_ENCODING = "UTF-8";

    @Bean
    public JobExecutionListener listener() {
        return jobCompletionNotificationListener;
    }

    @Bean
    public Job completeDataExport() throws IOException {
        List<BahmniForm> forms = formListProcessor.retrieveAllForms();
        FlowBuilder<FlowJobBuilder> completeDataExport = jobBuilderFactory.get(FULL_DATA_EXPORT_JOB_NAME)
                .incrementer(new RunIdIncrementer()).preventRestart()
                .listener(listener())
                .flow(treatmentRegistrationBaseExportStep.getStep());
//                .next(drugOrderBaseExportStep.getStep())
//                .next(otExportStep.getStep())
//                .next(bedManagementExportStep.getStep())
//                .next(appointmentSchedulingExportStep.getStep())
//                .next(metaDataCodeDictionaryExportStep.getStep());

//        for (BahmniForm form : forms) {
//            ObservationExportStep observationExportStep = observationExportStepFactory.getObject();
//            observationExportStep.setForm(form);
//            completeDataExport.next(observationExportStep.getStep());
//        }
        return completeDataExport.end().build();
    }


    @Bean
    public freemarker.template.Configuration freeMarkerConfiguration() throws IOException {
        freemarker.template.Configuration freemarkerTemplateConfig = new freemarker.template.Configuration(
                freemarker.template.Configuration.VERSION_2_3_22);
        freemarkerTemplateConfig.setDirectoryForTemplateLoading(freemarkerTemplateLocation.getFile());

        freemarkerTemplateConfig.setDefaultEncoding(DEFAULT_ENCODING);
        freemarkerTemplateConfig.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

        return freemarkerTemplateConfig;
    }

    @PreDestroy
    public void generateReport() {

    }
}
