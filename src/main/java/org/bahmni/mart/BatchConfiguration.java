package org.bahmni.mart;

import freemarker.template.TemplateExceptionHandler;
import org.bahmni.mart.config.FormStepConfigurer;
import org.bahmni.mart.config.ProgramDataStepConfigurer;
import org.bahmni.mart.config.StepConfigurer;
import org.bahmni.mart.config.job.JobDefinition;
import org.bahmni.mart.config.job.JobDefinitionReader;
import org.bahmni.mart.config.job.JobDefinitionValidator;
import org.bahmni.mart.exception.InvalidJobConfiguration;
import org.bahmni.mart.exports.SimpleJobTemplate;
import org.bahmni.mart.exports.TreatmentRegistrationBaseExportStep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.FlowJobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration extends DefaultBatchConfigurer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(BatchConfiguration.class);

    public static final String FULL_DATA_EXPORT_JOB_NAME = "ammanExports";
    private static final String DEFAULT_ENCODING = "UTF-8";

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private TreatmentRegistrationBaseExportStep treatmentRegistrationBaseExportStep;

    @Autowired
    private FormStepConfigurer formStepConfigurer;

    @Autowired
    private ProgramDataStepConfigurer programDataStepConfigurer;

    @Autowired
    private SimpleJobTemplate simpleJobTemplate;

    @Autowired
    private JobDefinitionReader jobDefinitionReader;

    @Autowired
    private JobLauncher jobLauncher;


    private List<StepConfigurer> stepConfigurers = new ArrayList<>();

    @Bean
    public Job completeDataExport() throws IOException {
        FlowBuilder<FlowJobBuilder> completeDataExport = jobBuilderFactory.get(FULL_DATA_EXPORT_JOB_NAME)
                .incrementer(new RunIdIncrementer()).preventRestart()
                .flow(treatmentRegistrationBaseExportStep.getStep());
        //TODO: Have to remove treatmentRegistrationBaseExportStep from flow

        setStepConfigurers();

        for (StepConfigurer stepConfigurer : stepConfigurers) {
            stepConfigurer.registerSteps(completeDataExport);
            stepConfigurer.createTables();
        }
        return completeDataExport.end().build();
    }

    private void setStepConfigurers() {
        stepConfigurers.add(formStepConfigurer);
        stepConfigurers.add(programDataStepConfigurer);
    }

    @Bean
    public freemarker.template.Configuration freeMarkerConfiguration() throws IOException {
        freemarker.template.Configuration freemarkerTemplateConfig = new freemarker.template.Configuration(
                freemarker.template.Configuration.VERSION_2_3_22);
        freemarkerTemplateConfig.setClassForTemplateLoading(this.getClass(), "/templates");
        freemarkerTemplateConfig.setDefaultEncoding(DEFAULT_ENCODING);
        freemarkerTemplateConfig.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

        return freemarkerTemplateConfig;
    }

    @PreDestroy
    public void generateReport() {

    }

    @Override
    public void run(String... args) {
        List<JobDefinition> jobDefinitions = jobDefinitionReader.getJobDefinitions();
        if (!JobDefinitionValidator.validate(jobDefinitions))
            throw new InvalidJobConfiguration();

        launchJobs(getJobs(jobDefinitions));
    }

    private void launchJobs(List<Job> jobs) {
        jobs.forEach(job -> {
            try {
                JobParametersBuilder jobParametersBuilder = new JobParametersBuilder();
                jobParametersBuilder.addDate(job.getName(), new Date());
                jobLauncher.run(job, jobParametersBuilder.toJobParameters());
            } catch (JobExecutionAlreadyRunningException | JobRestartException |
                    JobParametersInvalidException | JobInstanceAlreadyCompleteException e) {
                log.warn(e.getMessage());
                log.debug(e.getMessage(), e);
            }
        });
    }

    private List<Job> getJobs(List<JobDefinition> jobDefinitions) {
        return jobDefinitions.stream().map(jobDefinition -> simpleJobTemplate.buildJob(jobDefinition))
                .collect(Collectors.toList());
    }
}
