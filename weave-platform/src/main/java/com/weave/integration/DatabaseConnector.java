// FILE: src/main/java/com/weave/integration/DatabaseConnector.java

package com.weave.integration;

import com.weave.model.DataSet;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Database Connector for SQL databases
 * Supports PostgreSQL, MySQL, etc.
 */
public class DatabaseConnector extends DataConnector {
    private Connection connection;
    private String tableName;
    
    public DatabaseConnector() {
        super();
    }
    
    public DatabaseConnector(String jdbcUrl) {
        super();
        this.connectionString = jdbcUrl;
    }
    
    public DatabaseConnector(String jdbcUrl, String username, String password) {
        super();
        this.connectionString = jdbcUrl;
        this.config.put("username", username);
        this.config.put("password", password);
    }
    
    /**
     * Set table name for operations
     */
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
    
    @Override
    public void connect() throws SQLException {
        if (connectionString == null || connectionString.isEmpty()) {
            throw new IllegalArgumentException("JDBC URL cannot be null or empty");
        }
        
        String username = config.get("username");
        String password = config.get("password");
        
        if (username != null && password != null) {
            connection = DriverManager.getConnection(connectionString, username, password);
        } else {
            connection = DriverManager.getConnection(connectionString);
        }
        
        connected = true;
        System.out.println("Connected to database: " + connectionString);
    }
    
    @Override
    public DataSet read() throws SQLException {
        if (!connected) {
            throw new IllegalStateException("Not connected. Call connect() first.");
        }
        
        if (tableName == null || tableName.isEmpty()) {
            throw new IllegalArgumentException("Table name must be set before reading");
        }
        
        DataSet dataset = new DataSet();
        String query = "SELECT * FROM " + tableName;
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            ResultSetMetaData meta = rs.getMetaData();
            int columnCount = meta.getColumnCount();
            
            // Set schema
            for (int i = 1; i <= columnCount; i++) {
                dataset.setSchema(meta.getColumnName(i), meta.getColumnTypeName(i));
            }
            
            // Read rows
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    row.put(meta.getColumnName(i), rs.getObject(i));
                }
                dataset.addRow(row);
            }
            
            System.out.println("Read " + dataset.getRowCount() + " rows from table: " + tableName);
        }
        
        return dataset;
    }
    
    @Override
    public void write(DataSet data) throws SQLException {
        if (!connected) {
            throw new IllegalStateException("Not connected. Call connect() first.");
        }
        
        if (tableName == null || tableName.isEmpty()) {
            throw new IllegalArgumentException("Table name must be set before writing");
        }
        
        if (data.getRowCount() == 0) {
            System.out.println("No data to write");
            return;
        }
        
        // Build INSERT statement
        Map<String, Object> firstRow = data.getRows().get(0);
        StringBuilder sql = new StringBuilder("INSERT INTO " + tableName + " (");
        StringBuilder values = new StringBuilder("VALUES (");
        
        boolean first = true;
        for (String column : firstRow.keySet()) {
            if (!first) {
                sql.append(", ");
                values.append(", ");
            }
            sql.append(column);
            values.append("?");
            first = false;
        }
        
        sql.append(") ");
        values.append(")");
        sql.append(values);
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql.toString())) {
            for (Map<String, Object> row : data.getRows()) {
                int index = 1;
                for (Object value : row.values()) {
                    pstmt.setObject(index++, value);
                }
                pstmt.addBatch();
            }
            
            int[] results = pstmt.executeBatch();
            System.out.println("Wrote " + results.length + " rows to table: " + tableName);
        }
    }
    
    @Override
    public void disconnect() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
            connection = null;
            connected = false;
            System.out.println("Disconnected from database");
        }
    }
    
    /**
     * Execute a custom SQL query
     */
    public DataSet executeQuery(String sql) throws SQLException {
        if (!connected) {
            throw new IllegalStateException("Not connected. Call connect() first.");
        }
        
        DataSet dataset = new DataSet();
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            ResultSetMetaData meta = rs.getMetaData();
            int columnCount = meta.getColumnCount();
            
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    row.put(meta.getColumnName(i), rs.getObject(i));
                }
                dataset.addRow(row);
            }
        }
        
        return dataset;
    }
}
