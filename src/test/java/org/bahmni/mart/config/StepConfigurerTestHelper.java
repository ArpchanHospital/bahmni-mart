package org.bahmni.mart.config;

import org.bahmni.mart.config.job.JobDefinition;
import org.bahmni.mart.config.job.JobDefinitionReader;
import org.bahmni.mart.exports.ObservationExportStep;
import org.bahmni.mart.form.FormListProcessor;
import org.bahmni.mart.form.domain.Concept;
import org.bahmni.mart.form.service.ObsService;
import org.bahmni.mart.table.FormTableMetadataGenerator;
import org.bahmni.mart.table.TableGeneratorStep;
import org.mockito.Mock;
import org.springframework.beans.factory.ObjectFactory;

import static org.bahmni.mart.CommonTestHelper.setValuesForSuperClassMemberFields;

public class StepConfigurerTestHelper {

    @Mock
    protected TableGeneratorStep tableGeneratorStep;

    @Mock
    protected FormTableMetadataGenerator formTableMetadataGenerator;

    @Mock
    protected FormListProcessor formListProcessor;

    @Mock
    protected ObjectFactory<ObservationExportStep> observationExportStepFactory;

    @Mock
    protected ObsService obsService;

    @Mock
    protected JobDefinitionReader jobDefinitionReader;

    @Mock
    protected JobDefinition jobDefinition;

    @Mock
    protected Concept concept;

    public void setUp(StepConfigurer classInstance) throws Exception {
        setValuesForSuperClassMemberFields(classInstance, "tableGeneratorStep", tableGeneratorStep);
        setValuesForSuperClassMemberFields(classInstance,
                "formTableMetadataGenerator", formTableMetadataGenerator);
        setValuesForSuperClassMemberFields(classInstance,
                "observationExportStepFactory", observationExportStepFactory);
        setValuesForSuperClassMemberFields(classInstance, "formListProcessor", formListProcessor);
        setValuesForSuperClassMemberFields(classInstance, "jobDefinitionReader", jobDefinitionReader);
        setValuesForSuperClassMemberFields(classInstance, "obsService", obsService);
    }
}
