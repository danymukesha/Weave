
// FILE: src/main/java/com/weave/core/WeaveMessageBus.java

package com.weave.core;

import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.List;

/**
 * Core Message Bus - Central event routing system
 * Similar to TIBCO EMS (Enterprise Message Service)
 * Provides pub/sub messaging for asynchronous component communication
 */
public class WeaveMessageBus {
    private final ConcurrentHashMap<String, Queue<Message>> topics;
    private final ConcurrentHashMap<String, List<MessageHandler>> subscribers;
    private final ExecutorService executor;
    private volatile boolean running;
    
    public WeaveMessageBus() {
        this.topics = new ConcurrentHashMap<>();
        this.subscribers = new ConcurrentHashMap<>();
        this.executor = Executors.newFixedThreadPool(10);
        this.running = true;
    }
    
    /**
     * Publish a message to a topic
     * @param topic The topic name
     * @param message The message to publish
     */
    public void publish(String topic, Message message) {
        if (!running) {
            throw new IllegalStateException("Message bus is not running");
        }
        
        // Add message to topic queue
        topics.computeIfAbsent(topic, k -> new ConcurrentLinkedQueue<>()).add(message);
        
        // Notify all subscribers asynchronously
        notifySubscribers(topic, message);
    }
    
    /**
     * Subscribe to a topic with a message handler
     * @param topic The topic to subscribe to
     * @param handler The handler to process messages
     */
    public void subscribe(String topic, MessageHandler handler) {
        subscribers.computeIfAbsent(topic, k -> new CopyOnWriteArrayList<>()).add(handler);
    }
    
    /**
     * Unsubscribe a handler from a topic
     * @param topic The topic to unsubscribe from
     * @param handler The handler to remove
     */
    public void unsubscribe(String topic, MessageHandler handler) {
        List<MessageHandler> handlers = subscribers.get(topic);
        if (handlers != null) {
            handlers.remove(handler);
        }
    }
    
    /**
     * Get all messages from a topic
     * @param topic The topic name
     * @return Queue of messages
     */
    public Queue<Message> getMessages(String topic) {
        return topics.get(topic);
    }
    
    /**
     * Clear all messages from a topic
     * @param topic The topic to clear
     */
    public void clearTopic(String topic) {
        Queue<Message> queue = topics.get(topic);
        if (queue != null) {
            queue.clear();
        }
    }
    
    /**
     * Shutdown the message bus
     */
    public void shutdown() {
        running = false;
        executor.shutdown();
    }
    
    /**
     * Notify all subscribers of a topic about a new message
     */
    private void notifySubscribers(String topic, Message message) {
        List<MessageHandler> handlers = subscribers.get(topic);
        if (handlers != null && !handlers.isEmpty()) {
            for (MessageHandler handler : handlers) {
                executor.submit(() -> {
                    try {
                        handler.handle(message);
                    } catch (Exception e) {
                        System.err.println("Error handling message: " + e.getMessage());
                        e.printStackTrace();
                    }
                });
            }
        }
    }
    
    /**
     * Get the number of topics
     */
    public int getTopicCount() {
        return topics.size();
    }
    
    /**
     * Get the number of subscribers for a topic
     */
    public int getSubscriberCount(String topic) {
        List<MessageHandler> handlers = subscribers.get(topic);
        return handlers != null ? handlers.size() : 0;
    }
    
    /**
     * Check if the message bus is running
     */
    public boolean isRunning() {
        return running;
    }
}