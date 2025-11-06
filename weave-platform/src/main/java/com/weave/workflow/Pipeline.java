// FILE: src/main/java/com/weave/workflow/Pipeline.java

package com.weave.workflow;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Pipeline class representing a workflow
 */
public class Pipeline {
    private final String id;
    private String name;
    private String description;
    private String type;
    private List<PipelineStep> steps;
    private String status;
    
    public Pipeline(String name) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.steps = new ArrayList<>();
        this.status = "created";
    }
    
    public Pipeline(String name, String description) {
        this(name);
        this.description = description;
    }
    
    /**
     * Add a step to the pipeline
     */
    public void addStep(PipelineStep step) {
        if (step == null) {
            throw new IllegalArgumentException("Step cannot be null");
        }
        steps.add(step);
    }
    
    /**
     * Remove a step from the pipeline
     */
    public void removeStep(PipelineStep step) {
        steps.remove(step);
    }
    
    /**
     * Remove a step by index
     */
    public void removeStep(int index) {
        if (index >= 0 && index < steps.size()) {
            steps.remove(index);
        }
    }
    
    /**
     * Get step by index
     */
    public PipelineStep getStep(int index) {
        if (index >= 0 && index < steps.size()) {
            return steps.get(index);
        }
        return null;
    }
    
    /**
     * Get all steps
     */
    public List<PipelineStep> getSteps() {
        return new ArrayList<>(steps);
    }
    
    /**
     * Get step count
     */
    public int getStepCount() {
        return steps.size();
    }
    
    // Getters and setters
    
    public String getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    @Override
    public String toString() {
        return "Pipeline{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", steps=" + steps.size() +
                ", status='" + status + '\'' +
                '}';
    }
}
