package org.bahmni.mart.helper;

public enum AttributeColumnName {
    person_attribute_type("format"),
    visit_attribute_type("datatype"),
    provider_attribute_type("datatype");

    private String datatype;

    AttributeColumnName(String datatype) {
        this.datatype = datatype;
    }

    public String getDatatype() {
        return datatype;
    }
}
