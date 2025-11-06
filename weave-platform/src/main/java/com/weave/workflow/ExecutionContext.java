// FILE: src/main/java/com/weave/workflow/ExecutionContext.java

package com.weave.workflow;

import java.util.HashMap;
import java.util.Map;

/**
 * Execution context for pipeline execution
 * Stores inputs, outputs, and metadata
 */
public class ExecutionContext {
    private final Map<String, Object> inputs;
    private final Map<String, Object> outputs;
    private final Map<String, Object> metadata;
    
    public ExecutionContext() {
        this.inputs = new HashMap<>();
        this.outputs = new HashMap<>();
        this.metadata = new HashMap<>();
    }
    
    public ExecutionContext(Map<String, Object> inputs) {
        this.inputs = new HashMap<>(inputs);
        this.outputs = new HashMap<>();
        this.metadata = new HashMap<>();
    }
    
    // Input methods
    
    public Object getInput(String key) {
        return inputs.get(key);
    }
    
    public String getInputAsString(String key) {
        Object value = inputs.get(key);
        return value != null ? value.toString() : null;
    }
    
    public Integer getInputAsInteger(String key) {
        Object value = inputs.get(key);
        if (value instanceof Integer) {
            return (Integer) value;
        }
        return null;
    }
    
    public void setInput(String key, Object value) {
        inputs.put(key, value);
    }
    
    public Map<String, Object> getInputs() {
        return new HashMap<>(inputs);
    }
    
    // Output methods
    
    public Object getOutput(String key) {
        return outputs.get(key);
    }
    
    public void setOutput(String key, Object value) {
        outputs.put(key, value);
    }
    
    public Map<String, Object> getOutputs() {
        return new HashMap<>(outputs);
    }
    
    public void clearOutputs() {
        outputs.clear();
    }
    
    // Metadata methods
    
    public Object getMetadata(String key) {
        return metadata.get(key);
    }
    
    public void setMetadata(String key, Object value) {
        metadata.put(key, value);
    }
    
    public Map<String, Object> getAllMetadata() {
        return new HashMap<>(metadata);
    }
    
    // Utility methods
    
    public boolean hasInput(String key) {
        return inputs.containsKey(key);
    }
    
    public boolean hasOutput(String key) {
        return outputs.containsKey(key);
    }
    
    @Override
    public String toString() {
        return "ExecutionContext{" +
                "inputs=" + inputs.keySet() +
                ", outputs=" + outputs.keySet() +
                ", metadata=" + metadata.keySet() +
                '}';
    }
}
