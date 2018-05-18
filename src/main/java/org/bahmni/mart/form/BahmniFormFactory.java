package org.bahmni.mart.form;

import org.bahmni.mart.config.job.JobDefinition;
import org.bahmni.mart.form.domain.BahmniForm;
import org.bahmni.mart.form.domain.Concept;
import org.bahmni.mart.form.service.ConceptService;
import org.bahmni.mart.helper.IgnoreColumnsConfigHelper;
import org.bahmni.mart.helper.SeparateTableConfigHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;

@Component
public class BahmniFormFactory {

    @Autowired
    private ConceptService conceptService;

    @Autowired
    private SeparateTableConfigHelper separateTableConfigHelper;

    @Autowired
    private IgnoreColumnsConfigHelper ignoreColumnsConfigHelper;

    public BahmniForm createForm(Concept concept, BahmniForm parentForm, JobDefinition jobDefinition) {
        return createForm(concept, parentForm, 0,
                ignoreColumnsConfigHelper.getIgnoreConceptsForJob(jobDefinition),
                separateTableConfigHelper.getSeparateTableConceptsForJob(jobDefinition));
    }

    private BahmniForm createForm(Concept concept, BahmniForm parentForm, int depth, HashSet<Concept> ignoreConcepts,
                                  HashSet<Concept> separateTableConcepts) {
        BahmniForm bahmniForm = new BahmniForm();
        bahmniForm.setFormName(concept);
        bahmniForm.setDepthToParent(depth);
        bahmniForm.setParent(parentForm);
        bahmniForm.setRootForm(getRootFormFor(parentForm));

        constructFormFields(concept, bahmniForm, depth, ignoreConcepts, separateTableConcepts);
        return bahmniForm;
    }

    private BahmniForm getRootFormFor(BahmniForm form) {
        if (form == null) {
            return null;
        } else if (form.getDepthToParent() == 0) {
            return form;
        }
        return getRootFormFor(form.getParent());
    }

    private void constructFormFields(Concept concept, BahmniForm bahmniForm, int depth,
                                     HashSet<Concept> ignoreConcepts, HashSet<Concept> separateTableConcepts) {
        if (concept.getIsSet() == 0) {
            bahmniForm.addField(concept);
            return;
        }

        List<Concept> childConcepts = conceptService.getChildConcepts(concept.getName());
        int childDepth = depth + 1;
        for (Concept childConcept : childConcepts) {
            if (ignoreConcepts.contains(childConcept)) {
                continue;
            } else if (separateTableConcepts.contains(childConcept)) {
                bahmniForm.addChild(
                        createForm(childConcept, bahmniForm, childDepth, ignoreConcepts, separateTableConcepts));
            } else if (childConcept.getIsSet() == 0) {
                bahmniForm.addField(childConcept);
            } else {
                constructFormFields(childConcept, bahmniForm, childDepth, ignoreConcepts, separateTableConcepts);
            }
        }
    }

    public BahmniForm getFormWithAddMoreAndMultiSelectConceptsAlone(BahmniForm form) {

        BahmniForm formWithAddMoreAndMultiSelectsAlone = new BahmniForm();

        formWithAddMoreAndMultiSelectsAlone.setFormName(form.getFormName());
        for (Concept concept : form.getFields()) {
            if (separateTableConfigHelper.isAddMoreOrMultiSelect(concept)) {
                setParentConcept(form, concept);
                formWithAddMoreAndMultiSelectsAlone.addField(concept);
            }
        }
        setAddMoreConceptSets(formWithAddMoreAndMultiSelectsAlone);

        return formWithAddMoreAndMultiSelectsAlone;
    }

    private void setAddMoreConceptSets(BahmniForm formWithAddMoreAndMultiSelectsAlone) {
        List<Concept> childConcepts = conceptService.getChildConcepts(formWithAddMoreAndMultiSelectsAlone
                .getFormName().getName());
        for (Concept concept : childConcepts) {
            if (concept.getIsSet() == 1 && separateTableConfigHelper.isAddMore(concept.getName())) {
                setParentConcept(formWithAddMoreAndMultiSelectsAlone, concept);
                formWithAddMoreAndMultiSelectsAlone.addField(concept);
            }
        }
    }

    private void setParentConcept(BahmniForm form, Concept concept) {
        Concept immediateParentOfChildFromRootConcept = conceptService
                .getImmediateParentOfChildFromRootConcept(form.getFormName(), concept);
        concept.setParent(immediateParentOfChildFromRootConcept);

    }
}
