package com.example.generic_mapper.model;

import java.util.Map;

public class FieldDefinition {
    private String name;
    private String type;
    private boolean required;
    private String defaultValue;
    private boolean autoIncrement;
    private Map<String, Object> validation;

    // Getters and setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public boolean isRequired() { return required; }
    public void setRequired(boolean required) { this.required = required; }
    public String getDefaultValue() { return defaultValue; }
    public void setDefaultValue(String defaultValue) { this.defaultValue = defaultValue; }
    public boolean isAutoIncrement() { return autoIncrement; }
    public void setAutoIncrement(boolean autoIncrement) { this.autoIncrement = autoIncrement; }
    public Map<String, Object> getValidation() { return validation; }
    public void setValidation(Map<String, Object> validation) { this.validation = validation; }
}