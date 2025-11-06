// FILE: src/main/java/com/weave/core/MessageHandler.java

package com.weave.core;

/**
 * Functional interface for handling messages
 * Implementations define how to process messages from the message bus
 */
@FunctionalInterface
public interface MessageHandler {
    /**
     * Handle a message
     * @param message The message to handle
     * @throws Exception if message handling fails
     */
    void handle(Message message) throws Exception;
}