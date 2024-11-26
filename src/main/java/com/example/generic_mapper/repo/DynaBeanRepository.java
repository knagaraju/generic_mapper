package com.example.generic_mapper.repo;

import org.springframework.stereotype.Repository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Repository
public class DynaBeanRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void save(String tableName, Map<String, Object> data) {
        StringBuilder sql = new StringBuilder("INSERT INTO " + tableName + " (");
        StringBuilder values = new StringBuilder(") VALUES (");
        List<Object> parameters = new ArrayList<>();

        boolean first = true;
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            if (!first) {
                sql.append(", ");
                values.append(", ");
            }
            sql.append(entry.getKey());
            values.append("?");
            parameters.add(entry.getValue());
            first = false;
        }

        sql.append(values).append(")");

        Query query = entityManager.createNativeQuery(sql.toString());
        for (int i = 0; i < parameters.size(); i++) {
            query.setParameter(i + 1, parameters.get(i));
        }

        query.executeUpdate();
    }

    @Transactional
    public void update(String tableName, String idField, Object idValue, Map<String, Object> data) {
        StringBuilder sql = new StringBuilder("UPDATE " + tableName + " SET ");
        List<Object> parameters = new ArrayList<>();

        boolean first = true;
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            if (!entry.getKey().equals(idField)) {
                if (!first) {
                    sql.append(", ");
                }
                sql.append(entry.getKey()).append(" = ?");
                parameters.add(entry.getValue());
                first = false;
            }
        }

        sql.append(" WHERE ").append(idField).append(" = ?");
        parameters.add(idValue);

        Query query = entityManager.createNativeQuery(sql.toString());
        for (int i = 0; i < parameters.size(); i++) {
            query.setParameter(i + 1, parameters.get(i));
        }

        query.executeUpdate();
    }

    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    public List<Map<String, Object>> findAll(String tableName) {
        String sql = "SELECT * FROM " + tableName;
        Query query = entityManager.createNativeQuery(sql);
        List<Object[]> results = query.getResultList();

        return convertToMapList(results, getColumnNames(tableName));
    }

    private List<String> getColumnNames(String tableName) {
        String sql = "SELECT column_name FROM information_schema.columns " +
                "WHERE table_name = ?";
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter(1, tableName.toLowerCase());

        return query.getResultList();
    }

    private List<Map<String, Object>> convertToMapList(List<Object[]> results, List<String> columns) {
        List<Map<String, Object>> mapList = new ArrayList<>();
        for (Object[] row : results) {
            Map<String, Object> map = new HashMap<>();
            for (int i = 0; i < columns.size(); i++) {
                map.put(columns.get(i), row[i]);
            }
            mapList.add(map);
        }
        return mapList;
    }
}

