// FILE: src/main/java/com/weave/workflow/PipelineStep.java

package com.weave.workflow;

/**
 * Interface for pipeline steps
 */
public interface PipelineStep {
    /**
     * Get step name
     */
    String getName();
    
    /**
     * Get step description
     */
    default String getDescription() {
        return "";
    }
    
    /**
     * Execute the step
     * @param context Execution context containing inputs and outputs
     * @throws Exception if execution fails
     */
    void execute(ExecutionContext context) throws Exception;
    
    /**
     * Validate step configuration
     * @return true if configuration is valid
     */
    default boolean validate() {
        return true;
    }
}