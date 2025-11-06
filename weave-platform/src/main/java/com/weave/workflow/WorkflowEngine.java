// FILE: src/main/java/com/weave/workflow/WorkflowEngine.java

package com.weave.workflow;

import com.weave.core.WeaveMessageBus;
import com.weave.core.Message;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Workflow Engine - Executes multi-step pipelines
 * Similar to TIBCO BusinessWorks
 */
public class WorkflowEngine {
    private final WeaveMessageBus messageBus;
    private final Map<String, Pipeline> pipelines;
    private final ExecutorService executorService;
    private final Map<String, Future<ExecutionResult>> runningExecutions;
    
    public WorkflowEngine(WeaveMessageBus messageBus) {
        this.messageBus = messageBus;
        this.pipelines = new ConcurrentHashMap<>();
        this.executorService = Executors.newFixedThreadPool(5);
        this.runningExecutions = new ConcurrentHashMap<>();
    }
    
    /**
     * Register a pipeline
     */
    public void registerPipeline(String name, Pipeline pipeline) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Pipeline name cannot be null or empty");
        }
        if (pipeline == null) {
            throw new IllegalArgumentException("Pipeline cannot be null");
        }
        
        pipelines.put(name, pipeline);
        System.out.println("Registered pipeline: " + name + " with " + pipeline.getSteps().size() + " steps");
    }
    
    /**
     * Unregister a pipeline
     */
    public void unregisterPipeline(String name) {
        pipelines.remove(name);
        System.out.println("Unregistered pipeline: " + name);
    }
    
    /**
     * Get a registered pipeline
     */
    public Pipeline getPipeline(String name) {
        return pipelines.get(name);
    }
    
    /**
     * Execute a pipeline synchronously
     */
    public ExecutionResult execute(String pipelineName, Map<String, Object> inputs) {
        Pipeline pipeline = pipelines.get(pipelineName);
        if (pipeline == null) {
            return new ExecutionResult(false, "Pipeline not found: " + pipelineName, null);
        }
        
        System.out.println("Executing pipeline: " + pipelineName);
        
        // Create execution context
        ExecutionContext context = new ExecutionContext(inputs);
        context.setMetadata("pipeline_name", pipelineName);
        context.setMetadata("start_time", System.currentTimeMillis());
        
        // Publish start event
        messageBus.publish("workflow.started", 
            new Message.Builder()
                .type("workflow_started")
                .addPayload("pipeline", pipelineName)
                .build());
        
        // Execute each step
        for (PipelineStep step : pipeline.getSteps()) {
            try {
                System.out.println("Executing step: " + step.getName());
                
                // Publish step start event
                messageBus.publish("workflow.step.started", 
                    new Message.Builder()
                        .type("step_started")
                        .addPayload("pipeline", pipelineName)
                        .addPayload("step", step.getName())
                        .build());
                
                // Execute step
                step.execute(context);
                
                // Publish step completed event
                messageBus.publish("workflow.step.completed", 
                    new Message.Builder()
                        .type("step_completed")
                        .addPayload("pipeline", pipelineName)
                        .addPayload("step", step.getName())
                        .build());
                
            } catch (Exception e) {
                System.err.println("Error in step '" + step.getName() + "': " + e.getMessage());
                
                // Publish step failed event
                messageBus.publish("workflow.step.failed", 
                    new Message.Builder()
                        .type("step_failed")
                        .addPayload("pipeline", pipelineName)
                        .addPayload("step", step.getName())
                        .addPayload("error", e.getMessage())
                        .build());
                
                return new ExecutionResult(false, "Failed at step: " + step.getName(), e);
            }
        }
        
        // Add execution metadata
        context.setMetadata("end_time", System.currentTimeMillis());
        long duration = (Long) context.getMetadata("end_time") - (Long) context.getMetadata("start_time");
        context.setMetadata("duration_ms", duration);
        
        // Publish completed event
        messageBus.publish("workflow.completed", 
            new Message.Builder()
                .type("workflow_completed")
                .addPayload("pipeline", pipelineName)
                .addPayload("duration_ms", duration)
                .build());
        
        System.out.println("Pipeline completed successfully in " + duration + "ms");
        return new ExecutionResult(true, "Pipeline completed successfully", context.getOutputs());
    }
    
    /**
     * Execute a pipeline asynchronously
     */
    public String executeAsync(String pipelineName, Map<String, Object> inputs) {
        String executionId = java.util.UUID.randomUUID().toString();
        
        Future<ExecutionResult> future = executorService.submit(() -> {
            return execute(pipelineName, inputs);
        });
        
        runningExecutions.put(executionId, future);
        return executionId;
    }
    
    /**
     * Get result of asynchronous execution
     */
    public ExecutionResult getExecutionResult(String executionId) throws InterruptedException, ExecutionException {
        Future<ExecutionResult> future = runningExecutions.get(executionId);
        if (future == null) {
            return new ExecutionResult(false, "Execution not found: " + executionId, null);
        }
        
        if (future.isDone()) {
            return future.get();
        } else {
            return new ExecutionResult(false, "Execution still in progress", null);
        }
    }
    
    /**
     * Check if execution is complete
     */
    public boolean isExecutionComplete(String executionId) {
        Future<ExecutionResult> future = runningExecutions.get(executionId);
        return future != null && future.isDone();
    }
    
    /**
     * Get all registered pipeline names
     */
    public java.util.Set<String> getPipelineNames() {
        return pipelines.keySet();
    }
    
    /**
     * Shutdown the workflow engine
     */
    public void shutdown() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }
    }
}
