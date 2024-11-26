package com.example.generic_mapper.model;

import java.util.*;

public class TableDefinition {
    private List<FieldDefinition> fields;
    private Map<String, String> validations;
    private List<String> primaryKeys;

    public List<FieldDefinition> getFields() { return fields; }
    public void setFields(List<FieldDefinition> fields) { this.fields = fields; }
    public Map<String, String> getValidations() { return validations; }
    public void setValidations(Map<String, String> validations) { this.validations = validations; }
    public List<String> getPrimaryKeys() { return primaryKeys; }
    public void setPrimaryKeys(List<String> primaryKeys) { this.primaryKeys = primaryKeys; }

    public FieldDefinition getField(String name) {
        return fields.stream()
                .filter(f -> f.getName().equals(name))
                .findFirst()
                .orElse(null);
    }
}
