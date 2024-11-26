package com.example.generic_mapper.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import jakarta.annotation.PostConstruct;
import java.util.*;

@Configuration
@ConfigurationProperties(prefix = "database")
public class TableConfig {
    private Map<String, String> tables = new HashMap<>();
    private final ObjectMapper objectMapper;
    private Map<String, TableDefinition> tableDefinitions = new HashMap<>();

    public TableConfig(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void init() throws Exception {
        for (Map.Entry<String, String> entry : tables.entrySet()) {
            TableDefinition definition = objectMapper.readValue(
                    entry.getValue(),
                    TableDefinition.class
            );
            tableDefinitions.put(entry.getKey(), definition);
        }
    }

    public Map<String, String> getTables() {
        return tables;
    }

    public void setTables(Map<String, String> tables) {
        this.tables = tables;
    }

    public TableDefinition getTableDefinition(String tableName) {
        return tableDefinitions.get(tableName);
    }
}

