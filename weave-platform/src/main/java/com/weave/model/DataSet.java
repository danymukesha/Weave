// src/main/java/com/weave/model/DataSet.java

package com.weave.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DataSet class for storing tabular data
 */
public class DataSet {
    private List<Map<String, Object>> rows;
    private Map<String, String> schema;
    private Map<String, Object> metadata;
    
    public DataSet() {
        this.rows = new ArrayList<>();
        this.schema = new HashMap<>();
        this.metadata = new HashMap<>();
    }
    
    /**
     * Add a row to the dataset
     */
    public void addRow(Map<String, Object> row) {
        if (row != null) {
            rows.add(new HashMap<>(row));
        }
    }
    
    /**
     * Get all rows
     */
    public List<Map<String, Object>> getRows() {
        return new ArrayList<>(rows);
    }
    
    /**
     * Get row by index
     */
    public Map<String, Object> getRow(int index) {
        if (index >= 0 && index < rows.size()) {
            return new HashMap<>(rows.get(index));
        }
        return null;
    }
    
    /**
     * Get row count
     */
    public int getRowCount() {
        return rows.size();
    }
    
    /**
     * Set schema for a column
     */
    public void setSchema(String column, String type) {
        schema.put(column, type);
    }
    
    /**
     * Get schema
     */
    public Map<String, String> getSchema() {
        return new HashMap<>(schema);
    }
    
    /**
     * Get column type
     */
    public String getColumnType(String column) {
        return schema.get(column);
    }
    
    /**
     * Get all column names
     */
    public List<String> getColumnNames() {
        if (rows.isEmpty()) {
            return new ArrayList<>(schema.keySet());
        }
        return new ArrayList<>(rows.get(0).keySet());
    }
    
    /**
     * Set metadata
     */
    public void setMetadata(String key, Object value) {
        metadata.put(key, value);
    }
    
    /**
     * Get metadata
     */
    public Object getMetadata(String key) {
        return metadata.get(key);
    }
    
    /**
     * Get all metadata
     */
    public Map<String, Object> getAllMetadata() {
        return new HashMap<>(metadata);
    }
    
    /**
     * Clear all rows
     */
    public void clear() {
        rows.clear();
    }
    
    /**
     * Filter rows by predicate
     */
    public DataSet filter(java.util.function.Predicate<Map<String, Object>> predicate) {
        DataSet filtered = new DataSet();
        filtered.schema = new HashMap<>(this.schema);
        filtered.metadata = new HashMap<>(this.metadata);
        
        for (Map<String, Object> row : rows) {
            if (predicate.test(row)) {
                filtered.addRow(row);
            }
        }
        
        return filtered;
    }
    
    /**
     * Get column values as list
     */
    public List<Object> getColumn(String columnName) {
        List<Object> values = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            values.add(row.get(columnName));
        }
        return values;
    }
    
    @Override
    public String toString() {
        return "DataSet{" +
                "rows=" + rows.size() +
                ", columns=" + getColumnNames().size() +
                '}';
    }
}
