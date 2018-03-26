package org.bahmni.mart.form;

import org.bahmni.mart.CommonTestHelper;
import org.bahmni.mart.config.job.JobDefinitionReader;
import org.bahmni.mart.config.job.JobDefinitionUtil;
import org.bahmni.mart.form.domain.BahmniForm;
import org.bahmni.mart.form.domain.Concept;
import org.bahmni.mart.form.service.ObsService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.bahmni.mart.config.job.JobDefinitionUtil.getIgnoreConceptNamesForObsJob;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@PrepareForTest({JobDefinitionUtil.class})
@RunWith(PowerMockRunner.class)
public class FormListProcessorTest {

    @Mock
    private BahmniFormFactory bahmniFormFactory;

    @Mock
    private ObsService obsService;

    @Mock
    private JobDefinitionReader jobDefinitionReader;

    private FormListProcessor formListProcessor;

    @Before
    public void setUp() throws NoSuchFieldException, IllegalAccessException {
        initMocks(this);
        formListProcessor = new FormListProcessor();
        formListProcessor.setObsService(obsService);
        formListProcessor.setBahmniFormFactory(bahmniFormFactory);
        CommonTestHelper.setValuesForMemberFields(formListProcessor,"jobDefinitionReader",jobDefinitionReader);
        mockStatic(JobDefinitionUtil.class);
        when(jobDefinitionReader.getJobDefinitions()).thenReturn(Arrays.asList());
    }

    @Test
    public void shouldRetrieveAllForms() {
        Concept conceptA = new Concept(1, "a", 1);
        List<Concept> conceptList = new ArrayList();
        conceptList.add(conceptA);

        when(obsService.getChildConcepts(FormListProcessor.ALL_FORMS)).thenReturn(conceptList);
        when(getIgnoreConceptNamesForObsJob(any())).thenReturn(Arrays.asList());


        BahmniForm a11 = new BahmniFormBuilder().withName("a11").build();
        BahmniForm a12 = new BahmniFormBuilder().withName("a12").build();
        BahmniForm a13 = new BahmniFormBuilder().withName("a13").build();


        BahmniForm b11 = new BahmniFormBuilder().withName("b11").build();
        BahmniForm b12 = new BahmniFormBuilder().withName("b12").build();
        BahmniForm b13 = new BahmniFormBuilder().withName("b13").build();

        BahmniForm a1 = new BahmniFormBuilder().withName("a1").withChild(a11).withChild(a12).withChild(a13).build();
        BahmniForm b1 = new BahmniFormBuilder().withName("b1").withChild(b11).withChild(b12).withChild(b13).build();

        BahmniForm a = new BahmniFormBuilder().withName("a").withChild(a1).withChild(b1).build();

        when(bahmniFormFactory.createForm(conceptA, null)).thenReturn(a);

        List<BahmniForm> expected = Arrays.asList(a, a1, b1, a11, a12, a13, b11, b12, b13);

        List<BahmniForm> actual = formListProcessor.retrieveAllForms();

        assertEquals(expected.size(), actual.size());
        assertEquals(new HashSet(expected), new HashSet(actual));

    }

    @Test
    public void shouldRetrieveFormsDiscardingIgnoreConcepts() {
        Concept conceptA = new Concept(1, "formA", 1);
        Concept conceptB = new Concept(1,"formB",1);
        List<Concept> conceptList = new ArrayList();
        conceptList.add(conceptA);
        conceptList.add(conceptB);

        when(obsService.getChildConcepts(FormListProcessor.ALL_FORMS)).thenReturn(conceptList);
        when(getIgnoreConceptNamesForObsJob(any())).thenReturn(Arrays.asList("formB"));

        BahmniForm childFormOfA = new BahmniFormBuilder().withName("childFormOfA").build();
        BahmniForm childFormOfB = new BahmniFormBuilder().withName("childFormOfB").build();

        BahmniForm formA = new BahmniFormBuilder().withName("formA").withChild(childFormOfA).build();
        BahmniForm formB = new BahmniFormBuilder().withName("formB").withChild(childFormOfB).build();

        when(bahmniFormFactory.createForm(conceptA, null)).thenReturn(formA);
        when(bahmniFormFactory.createForm(conceptB, null)).thenReturn(formB);

        List<BahmniForm> expected = Arrays.asList(formA, childFormOfA);

        List<BahmniForm> actual = formListProcessor.retrieveAllForms();

        assertEquals(expected.size(), actual.size());
        assertEquals(expected, actual);

    }
}
