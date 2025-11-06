
// ============================================================================
// FILE: src/main/java/com/weave/integration/FileConnector.java
// ============================================================================

package com.weave.integration;

import com.weave.model.DataSet;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * File-based Connector for bioinformatics file formats
 * Supports FASTA, VCF, BAM, FASTQ, CSV, etc.
 */
public class FileConnector extends DataConnector {
    private BufferedReader reader;
    private BufferedWriter writer;
    private String fileFormat;
    
    public FileConnector() {
        super();
    }
    
    public FileConnector(String filePath) {
        super();
        this.connectionString = filePath;
    }
    
    public FileConnector(String filePath, String format) {
        super();
        this.connectionString = filePath;
        this.fileFormat = format;
    }
    
    /**
     * Set file format (FASTA, VCF, BAM, etc.)
     */
    public void setFileFormat(String format) {
        this.fileFormat = format;
    }
    
    @Override
    public void connect() throws IOException {
        if (connectionString == null || connectionString.isEmpty()) {
            throw new IllegalArgumentException("Connection string (file path) cannot be null or empty");
        }
        
        File file = new File(connectionString);
        if (!file.exists()) {
            throw new FileNotFoundException("File not found: " + connectionString);
        }
        
        reader = new BufferedReader(new FileReader(file));
        connected = true;
        System.out.println("Connected to file: " + connectionString);
    }
    
    @Override
    public DataSet read() throws IOException {
        if (!connected) {
            throw new IllegalStateException("Not connected. Call connect() first.");
        }
        
        DataSet dataset = new DataSet();
        dataset.setMetadata("source", connectionString);
        dataset.setMetadata("format", fileFormat != null ? fileFormat : "unknown");
        
        String line;
        int rowCount = 0;
        
        while ((line = reader.readLine()) != null) {
            // Skip empty lines and comments
            if (line.trim().isEmpty() || line.startsWith("#")) {
                continue;
            }
            
            Map<String, Object> row = parseLine(line);
            dataset.addRow(row);
            rowCount++;
        }
        
        System.out.println("Read " + rowCount + " rows from file");
        return dataset;
    }
    
    @Override
    public void write(DataSet data) throws IOException {
        if (connectionString == null || connectionString.isEmpty()) {
            throw new IllegalArgumentException("Connection string (file path) cannot be null or empty");
        }
        
        writer = new BufferedWriter(new FileWriter(connectionString));
        
        for (Map<String, Object> row : data.getRows()) {
            String formattedRow = formatRow(row);
            writer.write(formattedRow);
            writer.newLine();
        }
        
        writer.flush();
        System.out.println("Wrote " + data.getRowCount() + " rows to file: " + connectionString);
    }
    
    @Override
    public void disconnect() throws IOException {
        if (reader != null) {
            reader.close();
            reader = null;
        }
        if (writer != null) {
            writer.close();
            writer = null;
        }
        connected = false;
        System.out.println("Disconnected from file");
    }
    
    /**
     * Parse a line based on file format
     */
    private Map<String, Object> parseLine(String line) {
        Map<String, Object> row = new HashMap<>();
        
        if (fileFormat == null) {
            // Default: treat as tab-separated
            String[] parts = line.split("\t");
            for (int i = 0; i < parts.length; i++) {
                row.put("column_" + i, parts[i]);
            }
        } else {
            switch (fileFormat.toUpperCase()) {
                case "FASTA":
                    parseFastaLine(line, row);
                    break;
                case "VCF":
                    parseVcfLine(line, row);
                    break;
                case "CSV":
                    parseCsvLine(line, row);
                    break;
                default:
                    // Generic tab-separated
                    String[] parts = line.split("\t");
                    for (int i = 0; i < parts.length; i++) {
                        row.put("column_" + i, parts[i]);
                    }
            }
        }
        
        return row;
    }
    
    private void parseFastaLine(String line, Map<String, Object> row) {
        if (line.startsWith(">")) {
            row.put("type", "header");
            row.put("sequence_id", line.substring(1).trim());
        } else {
            row.put("type", "sequence");
            row.put("sequence", line.trim());
        }
    }
    
    private void parseVcfLine(String line, Map<String, Object> row) {
        String[] parts = line.split("\t");
        if (parts.length >= 8) {
            row.put("chrom", parts[0]);
            row.put("pos", parts[1]);
            row.put("id", parts[2]);
            row.put("ref", parts[3]);
            row.put("alt", parts[4]);
            row.put("qual", parts[5]);
            row.put("filter", parts[6]);
            row.put("info", parts[7]);
        }
    }
    
    private void parseCsvLine(String line, Map<String, Object> row) {
        String[] parts = line.split(",");
        for (int i = 0; i < parts.length; i++) {
            row.put("column_" + i, parts[i].trim());
        }
    }
    
    /**
     * Format a row for writing
     */
    private String formatRow(Map<String, Object> row) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        
        for (Map.Entry<String, Object> entry : row.entrySet()) {
            if (!first) {
                sb.append("\t");
            }
            sb.append(entry.getValue());
            first = false;
        }
        
        return sb.toString();
    }
}
