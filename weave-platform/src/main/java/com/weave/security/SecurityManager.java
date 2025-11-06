// FILE: src/main/java/com/weave/security/SecurityManager.java

package com.weave.security;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Security Manager - Handles authentication, authorization, and compliance
 */
public class SecurityManager {
    private final Map<String, User> users;
    private final Map<String, Role> roles;
    private final AuditLogger auditLogger;
    private final EncryptionService encryptionService;
    
    public SecurityManager() {
        this.users = new ConcurrentHashMap<>();
        this.roles = new ConcurrentHashMap<>();
        this.auditLogger = new AuditLogger();
        this.encryptionService = new EncryptionService();
        initializeDefaultRoles();
    }
    
    private void initializeDefaultRoles() {
        roles.put("ADMIN", new Role("ADMIN", "System Administrator", 
            Arrays.asList("READ", "WRITE", "DELETE", "MANAGE_USERS", "VIEW_AUDIT")));
        roles.put("RESEARCHER", new Role("RESEARCHER", "Research Scientist", 
            Arrays.asList("READ", "WRITE", "EXECUTE_PIPELINE")));
        roles.put("ANALYST", new Role("ANALYST", "Data Analyst", 
            Arrays.asList("READ", "VIEW_REPORTS")));
    }
    
    public boolean authenticate(String username, String password) {
        User user = users.get(username);
        if (user != null && user.validatePassword(password)) {
            auditLogger.log(new AuditEntry("LOGIN", username, "SUCCESS"));
            return true;
        }
        auditLogger.log(new AuditEntry("LOGIN", username, "FAILED"));
        return false;
    }
    
    public boolean authorize(String username, String permission) {
        User user = users.get(username);
        if (user == null) return false;
        
        Role role = roles.get(user.getRoleName());
        boolean authorized = role != null && role.hasPermission(permission);
        
        auditLogger.log(new AuditEntry("AUTHORIZATION", username, 
            permission + " - " + (authorized ? "GRANTED" : "DENIED")));
        
        return authorized;
    }
    
    public void createUser(String username, String password, String roleName) {
        User user = new User(username, encryptionService.hashPassword(password), roleName);
        users.put(username, user);
        auditLogger.log(new AuditEntry("USER_CREATE", "system", "Created user: " + username));
    }
    
    public AuditLogger getAuditLogger() {
        return auditLogger;
    }
    
    public EncryptionService getEncryptionService() {
        return encryptionService;
    }
}

class User {
    private final String username;
    private final String passwordHash;
    private final String roleName;
    private boolean enabled;
    
    public User(String username, String passwordHash, String roleName) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.roleName = roleName;
        this.enabled = true;
    }
    
    public String getUsername() { return username; }
    public String getRoleName() { return roleName; }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    
    public boolean validatePassword(String password) {
        return passwordHash.equals(hashPassword(password));
    }
    
    private String hashPassword(String password) {
        return Integer.toHexString(password.hashCode());
    }
}

class Role {
    private final String name;
    private final String description;
    private final List<String> permissions;
    
    public Role(String name, String description, List<String> permissions) {
        this.name = name;
        this.description = description;
        this.permissions = new ArrayList<>(permissions);
    }
    
    public String getName() { return name; }
    public boolean hasPermission(String permission) {
        return permissions.contains(permission);
    }
}
