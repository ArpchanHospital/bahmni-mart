package org.bahmni.mart.job;

import org.bahmni.mart.config.job.model.JobDefinition;
import org.bahmni.mart.config.stepconfigurer.FormStepConfigurer;
import org.springframework.batch.core.Job;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ObsJobStrategy extends StepRegister implements JobStrategy {

    private final FormStepConfigurer formStepConfigurer;

    @Autowired
    public ObsJobStrategy(FormStepConfigurer formStepConfigurer) {
        this.formStepConfigurer = formStepConfigurer;
    }

    @Override
    public Job getJob(JobDefinition jobDefinition) {
        return getJob(formStepConfigurer, jobDefinition);
    }
}
