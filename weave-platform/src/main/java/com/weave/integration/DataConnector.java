// FILE: src/main/java/com/weave/integration/DataConnector.java

package com.weave.integration;

import com.weave.model.DataSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Abstract Data Connector - Base for all data source integrations
 * Provides template for connecting to various data sources
 */
public abstract class DataConnector {
    protected String connectionString;
    protected Map<String, String> config;
    protected boolean connected;
    
    public DataConnector() {
        this.config = new HashMap<>();
        this.connected = false;
    }
    
    /**
     * Set connection string
     */
    public void setConnectionString(String connectionString) {
        this.connectionString = connectionString;
    }
    
    /**
     * Get connection string
     */
    public String getConnectionString() {
        return connectionString;
    }
    
    /**
     * Set configuration parameter
     */
    public void setConfig(String key, String value) {
        config.put(key, value);
    }
    
    /**
     * Get configuration parameter
     */
    public String getConfig(String key) {
        return config.get(key);
    }
    
    /**
     * Check if connected
     */
    public boolean isConnected() {
        return connected;
    }
    
    /**
     * Connect to the data source
     * @throws Exception if connection fails
     */
    public abstract void connect() throws Exception;
    
    /**
     * Read data from the source
     * @return DataSet containing the read data
     * @throws Exception if read operation fails
     */
    public abstract DataSet read() throws Exception;
    
    /**
     * Write data to the source
     * @param data The dataset to write
     * @throws Exception if write operation fails
     */
    public abstract void write(DataSet data) throws Exception;
    
    /**
     * Disconnect from the data source
     * @throws Exception if disconnection fails
     */
    public abstract void disconnect() throws Exception;
    
    /**
     * Test the connection
     * @return true if connection is valid
     */
    public boolean testConnection() {
        try {
            connect();
            disconnect();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

