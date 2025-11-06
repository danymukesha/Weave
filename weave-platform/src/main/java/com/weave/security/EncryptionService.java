// FILE: src/main/java/com/weave/security/EncryptionService.java

package com.weave.security;

import java.util.Base64;

public class EncryptionService {
    
    public String hashPassword(String password) {
        // Simplified hashing - use BCrypt or similar in production
        return Base64.getEncoder().encodeToString(password.getBytes());
    }
    
    public String encrypt(String data) {
        // Simplified encryption - use AES-256 in production
        return Base64.getEncoder().encodeToString(data.getBytes());
    }
    
    public String decrypt(String encryptedData) {
        // Simplified decryption
        return new String(Base64.getDecoder().decode(encryptedData));
    }
}
