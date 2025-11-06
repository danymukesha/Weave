// FILE: src/main/java/com/weave/integration/APIConnector.java

package com.weave.integration;

import com.weave.model.DataSet;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * REST API Connector for external bioinformatics databases
 * Supports NCBI, ENSEMBL, UCSC, PubChem, etc.
 */
public class APIConnector extends DataConnector {
    private String apiKey;
    private String endpoint;
    private Map<String, String> headers;
    
    public APIConnector() {
        super();
        this.headers = new HashMap<>();
    }
    
    public APIConnector(String baseUrl) {
        super();
        this.connectionString = baseUrl;
        this.headers = new HashMap<>();
    }
    
    /**
     * Set API key for authentication
     */
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
        headers.put("Authorization", "Bearer " + apiKey);
    }
    
    /**
     * Set endpoint path
     */
    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }
    
    /**
     * Add custom header
     */
    public void addHeader(String key, String value) {
        headers.put(key, value);
    }
    
    @Override
    public void connect() throws Exception {
        if (connectionString == null || connectionString.isEmpty()) {
            throw new IllegalArgumentException("API base URL cannot be null or empty");
        }
        
        // Test connection with a simple request
        URL url = new URL(connectionString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        
        int responseCode = conn.getResponseCode();
        conn.disconnect();
        
        if (responseCode >= 200 && responseCode < 300) {
            connected = true;
            System.out.println("Connected to API: " + connectionString);
        } else {
            throw new Exception("Failed to connect to API. Response code: " + responseCode);
        }
    }
    
    @Override
    public DataSet read() throws Exception {
        if (!connected) {
            throw new IllegalStateException("Not connected. Call connect() first.");
        }
        
        String fullUrl = connectionString;
        if (endpoint != null && !endpoint.isEmpty()) {
            fullUrl += "/" + endpoint;
        }
        
        URL url = new URL(fullUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        
        // Add headers
        for (Map.Entry<String, String> header : headers.entrySet()) {
            conn.setRequestProperty(header.getKey(), header.getValue());
        }
        
        int responseCode = conn.getResponseCode();
        
        if (responseCode >= 200 && responseCode < 300) {
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            
            while ((line = in.readLine()) != null) {
                response.append(line);
            }
            in.close();
            
            DataSet dataset = parseJsonResponse(response.toString());
            System.out.println("Successfully retrieved data from API");
            return dataset;
            
        } else {
            throw new Exception("API request failed. Response code: " + responseCode);
        }
    }
    
    @Override
    public void write(DataSet data) throws Exception {
        if (!connected) {
            throw new IllegalStateException("Not connected. Call connect() first.");
        }
        
        // POST implementation would go here
        throw new UnsupportedOperationException("Write operation not yet implemented for API connector");
    }
    
    @Override
    public void disconnect() throws Exception {
        connected = false;
        System.out.println("Disconnected from API");
    }
    
    /**
     * Parse JSON response into DataSet
     * Note: This is a simplified parser. In production, use a JSON library like Gson or Jackson
     */
    private DataSet parseJsonResponse(String json) {
        DataSet dataset = new DataSet();
        
        // Simplified JSON parsing - replace with proper JSON library in production
        Map<String, Object> row = new HashMap<>();
        row.put("response", json);
        dataset.addRow(row);
        
        return dataset;
    }
}