package org.bahmni.mart.executors;

import org.apache.commons.lang3.StringUtils;
import org.bahmni.mart.config.MartJSONReader;
import org.bahmni.mart.config.job.JobDefinitionReader;
import org.bahmni.mart.config.view.RegViewDefinition;
import org.bahmni.mart.config.view.ViewDefinition;
import org.bahmni.mart.config.view.ViewExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Order(value = MartExecutionOrder.VIEW)
public class MartViewExecutor implements MartExecutor {

    private static final String REGISTRATION_SECOND_PAGE = "Registration Second Page";

    @Autowired
    private JobDefinitionReader jobDefinitionReader;

    @Autowired
    private MartJSONReader martJSONReader;

    @Autowired
    private ViewExecutor viewExecutor;

    @Autowired
    private RegViewDefinition regViewDefinition;

    @Override
    public void execute() {

        List<ViewDefinition> viewDefinitions = martJSONReader.getViewDefinitions();

        if (!StringUtils.isEmpty(jobDefinitionReader.getJobDefinitionByName(REGISTRATION_SECOND_PAGE).getName())) {
            viewDefinitions.add(regViewDefinition.getDefinition());
        }
        viewExecutor.execute(viewDefinitions);
    }
}
