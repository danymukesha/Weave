// FILE: src/main/java/com/weave/core/Message.java

package com.weave.core;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Message class representing an event or data transfer
 * Immutable message object for thread-safe operations
 */
public class Message {
    private final String id;
    private final String type;
    private final Map<String, Object> payload;
    private final long timestamp;
    private final String source;
    
    /**
     * Constructor with type and payload
     */
    public Message(String type, Map<String, Object> payload) {
        this(type, payload, "system");
    }
    
    /**
     * Constructor with type, payload, and source
     */
    public Message(String type, Map<String, Object> payload, String source) {
        this.id = UUID.randomUUID().toString();
        this.type = type;
        this.payload = new HashMap<>(payload); // Defensive copy
        this.timestamp = System.currentTimeMillis();
        this.source = source;
    }
    
    /**
     * Get message ID
     */
    public String getId() {
        return id;
    }
    
    /**
     * Get message type
     */
    public String getType() {
        return type;
    }
    
    /**
     * Get message payload (immutable view)
     */
    public Map<String, Object> getPayload() {
        return new HashMap<>(payload);
    }
    
    /**
     * Get specific payload value
     */
    public Object getPayloadValue(String key) {
        return payload.get(key);
    }
    
    /**
     * Get message timestamp
     */
    public long getTimestamp() {
        return timestamp;
    }
    
    /**
     * Get message source
     */
    public String getSource() {
        return source;
    }
    
    @Override
    public String toString() {
        return "Message{" +
                "id='" + id + '\'' +
                ", type='" + type + '\'' +
                ", source='" + source + '\'' +
                ", timestamp=" + timestamp +
                ", payload=" + payload +
                '}';
    }
    
    /**
     * Builder pattern for creating messages
     */
    public static class Builder {
        private String type;
        private Map<String, Object> payload = new HashMap<>();
        private String source = "system";
        
        public Builder type(String type) {
            this.type = type;
            return this;
        }
        
        public Builder payload(Map<String, Object> payload) {
            this.payload = payload;
            return this;
        }
        
        public Builder addPayload(String key, Object value) {
            this.payload.put(key, value);
            return this;
        }
        
        public Builder source(String source) {
            this.source = source;
            return this;
        }
        
        public Message build() {
            if (type == null || type.isEmpty()) {
                throw new IllegalArgumentException("Message type cannot be null or empty");
            }
            return new Message(type, payload, source);
        }
    }
}
