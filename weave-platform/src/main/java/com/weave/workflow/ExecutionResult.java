// FILE: src/main/java/com/weave/workflow/ExecutionResult.java

package com.weave.workflow;

import java.util.HashMap;
import java.util.Map;

/**
 * Result of pipeline execution
 */
public class ExecutionResult {
    private final boolean success;
    private final String message;
    private final Object data;
    private final Exception exception;
    private final long timestamp;
    private final Map<String, Object> metadata;
    
    public ExecutionResult(boolean success, String message, Object data) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.exception = null;
        this.timestamp = System.currentTimeMillis();
        this.metadata = new HashMap<>();
    }
    
    public ExecutionResult(boolean success, String message, Exception exception) {
        this.success = success;
        this.message = message;
        this.data = null;
        this.exception = exception;
        this.timestamp = System.currentTimeMillis();
        this.metadata = new HashMap<>();
    }
    
    // Getters
    
    public boolean isSuccess() {
        return success;
    }
    
    public boolean isFailed() {
        return !success;
    }
    
    public String getMessage() {
        return message;
    }
    
    public Object getData() {
        return data;
    }
    
    public Exception getException() {
        return exception;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public Map<String, Object> getMetadata() {
        return new HashMap<>(metadata);
    }
    
    public void addMetadata(String key, Object value) {
        metadata.put(key, value);
    }
    
    @Override
    public String toString() {
        return "ExecutionResult{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", timestamp=" + timestamp +
                ", hasData=" + (data != null) +
                ", hasException=" + (exception != null) +
                '}';
    }
}