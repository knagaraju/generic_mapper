package com.example.generic_mapper.service;

import com.example.generic_mapper.config.TableConfig;
import com.example.generic_mapper.model.FieldDefinition;
import com.example.generic_mapper.model.TableDefinition;
import com.example.generic_mapper.repo.DynaBeanRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
public class DynaBeanService {
    private final DynaBeanRepository repository;
    private final TableConfig tableConfig;

    public DynaBeanService(DynaBeanRepository repository, TableConfig tableConfig) {
        this.repository = repository;
        this.tableConfig = tableConfig;
    }

    @Transactional
    public void save(String tableName, Map<String, Object> data) {
        TableDefinition definition = tableConfig.getTableDefinition(tableName);
        if (definition == null) {
            throw new IllegalArgumentException("Unknown table: " + tableName);
        }

        validateData(data, definition);
        Map<String, Object> processedData = processData(data, definition);
        repository.save(tableName, processedData);
    }

    @Transactional
    public void update(String tableName, String idField, Object idValue, Map<String, Object> data) {
        TableDefinition definition = tableConfig.getTableDefinition(tableName);
        if (definition == null) {
            throw new IllegalArgumentException("Unknown table: " + tableName);
        }

        validateData(data, definition);
        Map<String, Object> processedData = processData(data, definition);
        repository.update(tableName, idField, idValue, processedData);
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> findAll(String tableName) {
        TableDefinition definition = tableConfig.getTableDefinition(tableName);
        if (definition == null) {
            throw new IllegalArgumentException("Unknown table: " + tableName);
        }

        return repository.findAll(tableName);
    }

    private void validateData(Map<String, Object> data, TableDefinition definition) {
        for (FieldDefinition field : definition.getFields()) {
            Object value = data.get(field.getName());

            if (field.isRequired() && value == null && !field.isAutoIncrement()) {
                throw new IllegalArgumentException(
                        "Required field missing: " + field.getName()
                );
            }

            if (value != null) {
                validateValue(value, field);
            }
        }
    }

    private void validateValue(Object value, FieldDefinition field) {
        Map<String, Object> validation = field.getValidation();
        if (validation == null) return;

        String valueStr = value.toString();

        // Length validation
        if (validation.containsKey("minLength")) {
            int minLength = ((Integer) validation.get("minLength"));
            if (valueStr.length() < minLength) {
                throw new IllegalArgumentException(
                        "Field " + field.getName() + " is too short"
                );
            }
        }

        if (validation.containsKey("maxLength")) {
            int maxLength = ((Integer) validation.get("maxLength"));
            if (valueStr.length() > maxLength) {
                throw new IllegalArgumentException(
                        "Field " + field.getName() + " is too long"
                );
            }
        }

        // Pattern validation
        if (validation.containsKey("pattern")) {
            String pattern = (String) validation.get("pattern");
            if (!valueStr.matches(pattern)) {
                throw new IllegalArgumentException(
                        "Field " + field.getName() + " does not match required pattern"
                );
            }
        }

        // Numeric validation
        if (field.getType().equals("integer") || field.getType().equals("decimal")) {
            double numValue = Double.parseDouble(valueStr);

            if (validation.containsKey("min")) {
                double min = Double.parseDouble(validation.get("min").toString());
                if (numValue < min) {
                    throw new IllegalArgumentException(
                            "Field " + field.getName() + " is too small"
                    );
                }
            }

            if (validation.containsKey("max")) {
                double max = Double.parseDouble(validation.get("max").toString());
                if (numValue > max) {
                    throw new IllegalArgumentException(
                            "Field " + field.getName() + " is too large"
                    );
                }
            }
        }

        // Enum validation
        if (validation.containsKey("enum")) {
            @SuppressWarnings("unchecked")
            List<String> enumValues = (List<String>) validation.get("enum");
            if (!enumValues.contains(valueStr)) {
                throw new IllegalArgumentException(
                        "Invalid enum value for field " + field.getName()
                );
            }
        }
    }

    private Map<String, Object> processData(Map<String, Object> data, TableDefinition definition) {
        Map<String, Object> processed = new HashMap<>();

        for (FieldDefinition field : definition.getFields()) {
            String fieldName = field.getName();
            Object value = data.get(fieldName);

            if (value == null && field.getDefaultValue() != null) {
                if (field.getDefaultValue().equals("CURRENT_TIMESTAMP")) {
                    processed.put(fieldName, new java.sql.Timestamp(System.currentTimeMillis()));
                } else {
                    processed.put(fieldName, convertValue(field.getDefaultValue(), field.getType()));
                }
            } else if (value != null) {
                processed.put(fieldName, convertValue(value, field.getType()));
            }
        }

        return processed;
    }

    private Object convertValue(Object value, String type) {
        if (value == null) return null;

        String valueStr = value.toString();
        switch (type.toLowerCase()) {
            case "string":
                return valueStr;
            case "integer":
                return Integer.parseInt(valueStr);
            case "long":
                return Long.parseLong(valueStr);
            case "decimal":
                return Double.parseDouble(valueStr);
            case "boolean":
                return Boolean.parseBoolean(valueStr);
            case "timestamp":
                return new java.sql.Timestamp(System.currentTimeMillis());
            default:
                return value;
        }
    }
}
