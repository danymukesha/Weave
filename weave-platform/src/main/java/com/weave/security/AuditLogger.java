// FILE: src/main/java/com/weave/security/AuditLogger.java

package com.weave.security;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class AuditLogger {
    private final List<AuditEntry> auditLog;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    public AuditLogger() {
        this.auditLog = new CopyOnWriteArrayList<>();
    }
    
    public void log(AuditEntry entry) {
        auditLog.add(entry);
        System.out.println("[AUDIT] " + entry);
    }
    
    public List<AuditEntry> getAuditLog() {
        return new ArrayList<>(auditLog);
    }
    
    public List<AuditEntry> getLogsByUser(String username) {
        List<AuditEntry> userLogs = new ArrayList<>();
        for (AuditEntry entry : auditLog) {
            if (entry.getUsername().equals(username)) {
                userLogs.add(entry);
            }
        }
        return userLogs;
    }
}

class AuditEntry {
    private final String action;
    private final String username;
    private final String details;
    private final LocalDateTime timestamp;
    
    public AuditEntry(String action, String username, String details) {
        this.action = action;
        this.username = username;
        this.details = details;
        this.timestamp = LocalDateTime.now();
    }
    
    public String getAction() { return action; }
    public String getUsername() { return username; }
    public String getDetails() { return details; }
    public LocalDateTime getTimestamp() { return timestamp; }
    
    @Override
    public String toString() {
        return String.format("[%s] %s by %s: %s", 
            timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
            action, username, details);
    }
}
