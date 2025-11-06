// FILE: src/main/java/com/weave/WeaveApplication.java

package com. weave;

import com.weave.core.WeaveMessageBus;
import com.weave.core.Message;
import com.weave.workflow.*;
import com.weave.integration.*;
import com.weave.model.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Weave Platform - Main Application
 * Enterprise Bioinformatics Integration and Analytics Platform
 */
public class WeaveApplication {
    
    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("  Weave™ Platform v1.0.0");
        System.out.println("  Enterprise Bioinformatics Integration");
        System.out.println("=================================================\n");
        
        // Initialize core components
        System.out.println("Initializing core components...");
        WeaveMessageBus messageBus = new WeaveMessageBus();
        WorkflowEngine workflowEngine = new WorkflowEngine(messageBus);
        
        // Subscribe to workflow events
        setupEventListeners(messageBus);
        
        // Create and register example pipelines
        registerExamplePipelines(workflowEngine);
        
        // Example 1: RNA-Seq Analysis Pipeline
        System.out.println("\n--- Example 1: RNA-Seq Analysis ---");
        executeRNASeqExample(workflowEngine);
        
        // Example 2: File Integration
        System.out.println("\n--- Example 2: File Integration ---");
        executeFileIntegrationExample();
        
        // Example 3: Data Model Usage
        System.out.println("\n--- Example 3: Sample Management ---");
        executeSampleManagementExample();
        
        // Shutdown
        System.out.println("\n=================================================");
        System.out.println("Shutting down Weave Platform...");
        workflowEngine.shutdown();
        messageBus.shutdown();
        System.out.println("Shutdown complete. Goodbye!");
        System.out.println("=================================================");
    }
    
    /**
     * Setup event listeners for workflow events
     */
    private static void setupEventListeners(WeaveMessageBus messageBus) {
        messageBus.subscribe("workflow.started", msg -> {
            System.out.println("[EVENT] Workflow started: " + msg.getPayloadValue("pipeline"));
        });
        
        messageBus.subscribe("workflow.step.started", msg -> {
            System.out.println("[EVENT] Step started: " + msg.getPayloadValue("step"));
        });
        
        messageBus.subscribe("workflow.step.completed", msg -> {
            System.out.println("[EVENT] Step completed: " + msg.getPayloadValue("step"));
        });
        
        messageBus.subscribe("workflow.step.failed", msg -> {
            System.out.println("[EVENT] Step failed: " + msg.getPayloadValue("step") + 
                             " - Error: " + msg.getPayloadValue("error"));
        });
        
        messageBus.subscribe("workflow.completed", msg -> {
            System.out.println("[EVENT] Workflow completed in " + 
                             msg.getPayloadValue("duration_ms") + "ms");
        });
    }
    
    /**
     * Register example pipelines
     */
    private static void registerExamplePipelines(WorkflowEngine workflowEngine) {
        // RNA-Seq Analysis Pipeline
        Pipeline rnaseqPipeline = createRNASeqPipeline();
        workflowEngine.registerPipeline("RNA-Seq-Analysis", rnaseqPipeline);
        
        // Variant Calling Pipeline
        Pipeline variantPipeline = createVariantCallingPipeline();
        workflowEngine.registerPipeline("Variant-Calling", variantPipeline);
        
        System.out.println("Registered " + workflowEngine.getPipelineNames().size() + " pipelines");
    }
    
    /**
     * Create RNA-Seq analysis pipeline
     */
    private static Pipeline createRNASeqPipeline() {
        Pipeline pipeline = new Pipeline("RNA-Seq Analysis");
        pipeline.setDescription("Complete RNA-Seq analysis workflow");
        pipeline.setType("transcriptomics");
        
        // Step 1: Quality Control
        pipeline.addStep(new PipelineStep() {
            @Override
            public String getName() {
                return "Quality Control";
            }
            
            @Override
            public String getDescription() {
                return "Run FastQC on input FASTQ files";
            }
            
            @Override
            public void execute(ExecutionContext ctx) throws Exception {
                System.out.println("  Running FastQC...");
                String inputFile = ctx.getInputAsString("input_file");
                System.out.println("  Input: " + inputFile);
                
                // Simulate processing
                Thread.sleep(500);
                
                ctx.setOutput("qc_passed", true);
                ctx.setOutput("qc_report", "/results/fastqc_report.html");
                System.out.println("  Quality control passed");
            }
        });
        
        // Step 2: Alignment
        pipeline.addStep(new PipelineStep() {
            @Override
            public String getName() {
                return "Alignment";
            }
            
            @Override
            public String getDescription() {
                return "Align reads using STAR";
            }
            
            @Override
            public void execute(ExecutionContext ctx) throws Exception {
                System.out.println("  Running STAR aligner...");
                String referenceGenome = ctx.getInputAsString("reference_genome");
                System.out.println("  Reference: " + referenceGenome);
                
                // Simulate alignment
                Thread.sleep(1000);
                
                ctx.setOutput("alignment_file", "/results/aligned.bam");
                ctx.setOutput("alignment_stats", "95% mapped");
                System.out.println("  Alignment complete: 95% reads mapped");
            }
        });
        
        // Step 3: Quantification
        pipeline.addStep(new PipelineStep() {
            @Override
            public String getName() {
                return "Quantification";
            }
            
            @Override
            public String getDescription() {
                return "Quantify gene expression using featureCounts";
            }
            
            @Override
            public void execute(ExecutionContext ctx) throws Exception {
                System.out.println("  Running featureCounts...");
                String bamFile = (String) ctx.getOutput("alignment_file");
                System.out.println("  Processing: " + bamFile);
                
                // Simulate quantification
                Thread.sleep(800);
                
                ctx.setOutput("counts_file", "/results/gene_counts.txt");
                ctx.setOutput("genes_quantified", 20000);
                System.out.println("  Quantified 20,000 genes");
            }
        });
        
        // Step 4: Differential Expression
        pipeline.addStep(new PipelineStep() {
            @Override
            public String getName() {
                return "Differential Expression";
            }
            
            @Override
            public String getDescription() {
                return "Identify differentially expressed genes using DESeq2";
            }
            
            @Override
            public void execute(ExecutionContext ctx) throws Exception {
                System.out.println("  Running DESeq2...");
                
                // Simulate DE analysis
                Thread.sleep(600);
                
                ctx.setOutput("de_results", "/results/differential_expression.csv");
                ctx.setOutput("significant_genes", 450);
                System.out.println("  Found 450 significantly expressed genes");
            }
        });
        
        return pipeline;
    }
    
    /**
     * Create Variant Calling pipeline
     */
    private static Pipeline createVariantCallingPipeline() {
        Pipeline pipeline = new Pipeline("Variant Calling");
        pipeline.setDescription("GATK-based variant calling workflow");
        pipeline.setType("genomics");
        
        pipeline.addStep(new PipelineStep() {
            @Override
            public String getName() {
                return "BWA Alignment";
            }
            
            @Override
            public void execute(ExecutionContext ctx) throws Exception {
                System.out.println("  Running BWA-MEM alignment...");
                Thread.sleep(500);
                ctx.setOutput("aligned_bam", "/results/aligned.bam");
            }
        });
        
        pipeline.addStep(new PipelineStep() {
            @Override
            public String getName() {
                return "Mark Duplicates";
            }
            
            @Override
            public void execute(ExecutionContext ctx) throws Exception {
                System.out.println("  Marking duplicate reads...");
                Thread.sleep(300);
                ctx.setOutput("dedup_bam", "/results/dedup.bam");
            }
        });
        
        pipeline.addStep(new PipelineStep() {
            @Override
            public String getName() {
                return "Variant Calling";
            }
            
            @Override
            public void execute(ExecutionContext ctx) throws Exception {
                System.out.println("  Calling variants with GATK HaplotypeCaller...");
                Thread.sleep(700);
                ctx.setOutput("vcf_file", "/results/variants.vcf");
                ctx.setOutput("variant_count", 15234);
            }
        });
        
        return pipeline;
    }
    
    /**
     * Execute RNA-Seq example
     */
    private static void executeRNASeqExample(WorkflowEngine workflowEngine) {
        Map<String, Object> inputs = new HashMap<>();
        inputs.put("input_file", "/data/sample.fastq");
        inputs.put("reference_genome", "hg38");
        
        ExecutionResult result = workflowEngine.execute("RNA-Seq-Analysis", inputs);
        
        if (result.isSuccess()) {
            System.out.println("\n✓ Pipeline completed successfully");
            System.out.println("Results: " + result.getData());
        } else {
            System.err.println("\n✗ Pipeline failed: " + result.getMessage());
        }
    }
    
    /**
     * Execute file integration example
     */
    private static void executeFileIntegrationExample() {
        try {
            // Create a sample file for demonstration
            java.io.File tempFile = java.io.File.createTempFile("sample", ".txt");
            try (java.io.PrintWriter writer = new java.io.PrintWriter(tempFile)) {
                writer.println("gene_id\texpression\tsample");
                writer.println("GENE001\t150.5\tsample1");
                writer.println("GENE002\t75.3\tsample1");
                writer.println("GENE003\t200.8\tsample1");
            }
            
            FileConnector connector = new FileConnector(tempFile.getAbsolutePath(), "CSV");
            connector.connect();
            
            DataSet data = connector.read();
            System.out.println("Read dataset: " + data.getRowCount() + " rows");
            
            connector.disconnect();
            tempFile.delete();
            
            System.out.println("✓ File integration successful");
            
        } catch (Exception e) {
            System.err.println("✗ File integration failed: " + e.getMessage());
        }
    }
    
    /**
     * Execute sample management example
     */
    private static void executeSampleManagementExample() {
        // Create samples
        Sample sample1 = new Sample("SAMPLE001", "Homo sapiens");
        sample1.setTissue("Liver");
        sample1.setCellType("Hepatocyte");
        sample1.setMetadata("age", 45);
        sample1.setMetadata("condition", "healthy");
        
        Sample sample2 = new Sample("SAMPLE002", "Homo sapiens");
        sample2.setTissue("Liver");
        sample2.setCellType("Hepatocyte");
        sample2.setMetadata("age", 52);
        sample2.setMetadata("condition", "diseased");
        
        System.out.println("Created samples:");
        System.out.println("  " + sample1);
        System.out.println("  " + sample2);
        
        // Create dataset with sample information
        DataSet sampleData = new DataSet();
        Map<String, Object> row1 = new HashMap<>();
        row1.put("sample_id", sample1.getId());
        row1.put("name", sample1.getName());
        row1.put("organism", sample1.getOrganism());
        row1.put("tissue", sample1.getTissue());
        sampleData.addRow(row1);
        
        Map<String, Object> row2 = new HashMap<>();
        row2.put("sample_id", sample2.getId());
        row2.put("name", sample2.getName());
        row2.put("organism", sample2.getOrganism());
        row2.put("tissue", sample2.getTissue());
        sampleData.addRow(row2);
        
        System.out.println("\nDataSet contains " + sampleData.getRowCount() + " samples");
        System.out.println("✓ Sample management successful");
    }
}