// FILE: src/main/java/com/weave/model/Sample.java

package com.weave.model;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Sample class representing a biological sample
 */
public class Sample {
    private final String id;
    private String name;
    private String organism;
    private String tissue;
    private String cellType;
    private String experimentId;
    private Map<String, Object> metadata;
    private long createdTimestamp;
    private long modifiedTimestamp;
    
    public Sample(String name, String organism) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.organism = organism;
        this.metadata = new HashMap<>();
        this.createdTimestamp = System.currentTimeMillis();
        this.modifiedTimestamp = this.createdTimestamp;
    }
    
    public Sample(String id, String name, String organism) {
        this.id = id;
        this.name = name;
        this.organism = organism;
        this.metadata = new HashMap<>();
        this.createdTimestamp = System.currentTimeMillis();
        this.modifiedTimestamp = this.createdTimestamp;
    }
    
    // Getters and setters
    
    public String getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
        this.modifiedTimestamp = System.currentTimeMillis();
    }
    
    public String getOrganism() {
        return organism;
    }
    
    public void setOrganism(String organism) {
        this.organism = organism;
        this.modifiedTimestamp = System.currentTimeMillis();
    }
    
    public String getTissue() {
        return tissue;
    }
    
    public void setTissue(String tissue) {
        this.tissue = tissue;
        this.modifiedTimestamp = System.currentTimeMillis();
    }
    
    public String getCellType() {
        return cellType;
    }
    
    public void setCellType(String cellType) {
        this.cellType = cellType;
        this.modifiedTimestamp = System.currentTimeMillis();
    }
    
    public String getExperimentId() {
        return experimentId;
    }
    
    public void setExperimentId(String experimentId) {
        this.experimentId = experimentId;
        this.modifiedTimestamp = System.currentTimeMillis();
    }
    
    public Map<String, Object> getMetadata() {
        return new HashMap<>(metadata);
    }
    
    public Object getMetadataValue(String key) {
        return metadata.get(key);
    }
    
    public void setMetadata(String key, Object value) {
        metadata.put(key, value);
        this.modifiedTimestamp = System.currentTimeMillis();
    }
    
    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = new HashMap<>(metadata);
        this.modifiedTimestamp = System.currentTimeMillis();
    }
    
    public long getCreatedTimestamp() {
        return createdTimestamp;
    }
    
    public long getModifiedTimestamp() {
        return modifiedTimestamp;
    }
    
    @Override
    public String toString() {
        return "Sample{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", organism='" + organism + '\'' +
                ", tissue='" + tissue + '\'' +
                ", cellType='" + cellType + '\'' +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sample sample = (Sample) o;
        return id.equals(sample.id);
    }
    
    @Override
    public int hashCode() {
        return id.hashCode();
    }
}