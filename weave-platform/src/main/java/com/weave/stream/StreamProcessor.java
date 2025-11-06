// FILE: src/main/java/com/eave/stream/StreamProcessor.java
// ============================================================================

package com.weave.stream;

import com.weave.core.WeaveMessageBus;
import com.weave.core.Message;
import java.util.*;
import java.util.concurrent.*;

/**
 * Stream Processor - Real-time event processing
 */
public class StreamProcessor {
    private final WeaveMessageBus messageBus;
    private final Map<String, StreamRule> rules;
    private final ExecutorService executor;
    private volatile boolean running;
    
    public StreamProcessor(WeaveMessageBus messageBus) {
        this.messageBus = messageBus;
        this.rules = new ConcurrentHashMap<>();
        this.executor = Executors.newFixedThreadPool(4);
        this.running = false;
    }
    
    public void addRule(StreamRule rule) {
        rules.put(rule.getId(), rule);
    }
    
    public void start() {
        running = true;
        messageBus.subscribe("stream.data", this::processStreamData);
        System.out.println("Stream Processor started");
    }
    
    public void stop() {
        running = false;
        executor.shutdown();
        System.out.println("Stream Processor stopped");
    }
    
    private void processStreamData(Message message) {
        if (!running) return;
        
        executor.submit(() -> {
            for (StreamRule rule : rules.values()) {
                if (rule.matches(message)) {
                    rule.execute(message, messageBus);
                }
            }
        });
    }
}

class StreamRule {
    private final String id;
    private final String name;
    private final RuleMatcher matcher;
    private final RuleAction action;
    
    public StreamRule(String name, RuleMatcher matcher, RuleAction action) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.matcher = matcher;
        this.action = action;
    }
    
    public String getId() { return id; }
    public String getName() { return name; }
    
    public boolean matches(Message message) {
        return matcher.test(message);
    }
    
    public void execute(Message message, WeaveMessageBus messageBus) {
        action.perform(message, messageBus);
    }
}

@FunctionalInterface
interface RuleMatcher {
    boolean test(Message message);
}

@FunctionalInterface
interface RuleAction {
    void perform(Message message, WeaveMessageBus messageBus);
}
