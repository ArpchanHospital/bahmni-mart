package org.bahmni.mart.form;

import org.bahmni.mart.form.domain.BahmniForm;
import org.bahmni.mart.form.domain.Concept;
import org.bahmni.mart.form.domain.Obs;
import org.springframework.batch.item.file.transform.FieldExtractor;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ObsFieldExtractor implements FieldExtractor<List<Obs>> {

    private BahmniForm form;

    public ObsFieldExtractor(BahmniForm form) {
        this.form = form;
    }

    @Override
    public Object[] extract(List<Obs> obsList) {
        List<Object> row = new ArrayList<>();

        if (obsList.isEmpty())
            return row.toArray();

        Map<Concept, String> obsRow = obsList.stream().collect(Collectors.toMap(Obs::getField, Obs::getValue));

        Obs firstObs = obsList.get(0);
        row.add(firstObs.getId());

        if (form.getParent() != null)
            row.add(firstObs.getParentId());

        form.getFields().forEach(field -> row.add(formatObsValue(obsRow.get(field))));

        return row.toArray();
    }

    private String formatObsValue(String text) {
        return StringUtils.isEmpty(text) ? text : text.replaceAll("[\n\t,]", " ");
    }
}
