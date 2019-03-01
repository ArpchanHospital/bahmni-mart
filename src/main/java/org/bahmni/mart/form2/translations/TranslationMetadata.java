package org.bahmni.mart.form2.translations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
class TranslationMetadata {

    private static final String  TRANSLATION_FILES_LOCATION_SQL = "SELECT property_value FROM global_property " +
            "WHERE property = 'bahmni.formTranslations.directory'";

    private final JdbcTemplate openmrsJdbcTemplate;

    @Autowired
    public TranslationMetadata(JdbcTemplate openmrsJdbcTemplate) {
        this.openmrsJdbcTemplate = openmrsJdbcTemplate;
    }

    String getTranslationsFilePath(String formName, int formVersion) {

        String fromTranslationsPath = openmrsJdbcTemplate.queryForObject(TRANSLATION_FILES_LOCATION_SQL, String.class);

        return String.format("%s/%s_%s.json", fromTranslationsPath, formName, formVersion);
    }
}
