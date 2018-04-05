package org.bahmni.mart;

import org.bahmni.mart.config.FormStepConfigurer;
import org.bahmni.mart.config.MartJSONReader;
import org.bahmni.mart.config.MetaDataStepConfigurer;
import org.bahmni.mart.config.StepConfigurer;
import org.bahmni.mart.config.job.JobDefinition;
import org.bahmni.mart.config.job.JobDefinitionReader;
import org.bahmni.mart.config.job.JobDefinitionValidator;
import org.bahmni.mart.config.view.ViewExecutor;
import org.bahmni.mart.exception.InvalidJobConfiguration;
import org.bahmni.mart.exports.TreatmentRegistrationBaseExportStep;
import org.bahmni.mart.exports.template.EAVJobTemplate;
import org.bahmni.mart.exports.template.SimpleJobTemplate;
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
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.isEmpty;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration extends DefaultBatchConfigurer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(BatchConfiguration.class);

    protected static final String OBS_DATA_FLATTENING_JOB_NAME = "flattenObs";

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private TreatmentRegistrationBaseExportStep treatmentRegistrationBaseExportStep;

    @Autowired
    private FormStepConfigurer formStepConfigurer;

    @Autowired
    private SimpleJobTemplate simpleJobTemplate;

    @Autowired
    private JobDefinitionReader jobDefinitionReader;

    @Autowired
    private MartJSONReader martJSONReader;

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private MetaDataStepConfigurer metaDataStepConfigurer;

    @Autowired
    private EAVJobTemplate eavJobTemplate;

    @Autowired
    private ViewExecutor viewExecutor;

    private Job buildObsJob() {
        FlowBuilder<FlowJobBuilder> completeDataExport = jobBuilderFactory.get(OBS_DATA_FLATTENING_JOB_NAME)
                .incrementer(new RunIdIncrementer()).preventRestart()
                .flow(treatmentRegistrationBaseExportStep.getStep());
        //TODO: Have to remove treatmentRegistrationBaseExportStep from flow

        getStepConfigurers().forEach(stepConfigurer -> {
            stepConfigurer.registerSteps(completeDataExport);
            stepConfigurer.createTables();
        });

        return completeDataExport.end().build();
    }

    private List<StepConfigurer> getStepConfigurers() {
        ArrayList<StepConfigurer> stepConfigurers = new ArrayList<>();
        stepConfigurers.add(formStepConfigurer);

        if (!isEmpty(jobDefinitionReader.getConceptReferenceSource()))
            stepConfigurers.add(metaDataStepConfigurer);
        return stepConfigurers;
    }

    @Override
    public void run(String... args) {
        List<JobDefinition> jobDefinitions = jobDefinitionReader.getJobDefinitions();
        if (!JobDefinitionValidator.validate(jobDefinitions))
            throw new InvalidJobConfiguration();
        launchJobs(getJobs(jobDefinitions));
        viewExecutor.execute(martJSONReader.getViewDefinitions());
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
        return jobDefinitions.stream().map(this::getJobByType).filter(Objects::nonNull).collect(Collectors.toList());
    }

    private Job getJobByType(JobDefinition jobDefinition) {
        switch (jobDefinition.getType()) {
          case "obs":
              return buildObsJob();
          case "eav":
              return eavJobTemplate.buildJob(jobDefinition);
          default:
              return simpleJobTemplate.buildJob(jobDefinition);
        }
    }
}
