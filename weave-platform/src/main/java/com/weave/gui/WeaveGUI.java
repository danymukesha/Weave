// ============================================================================
// FILE: src/main/java/com/weave/gui/WeaveGUI.java
// ============================================================================

package com.weave.gui;

import com.weave.core.WeaveMessageBus;
import com.weave.workflow.WorkflowEngine;
import com.weave.security.SecurityManager;
import com.weave.stream.StreamProcessor;
import com.weave.analytics.AnalyticsEngine;

import javax.swing.*;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

/**
 * Weave GUI - Main Application Window using Swing
 */
public class WeaveGUI extends JFrame {
    private final WeaveMessageBus messageBus;
    private final WorkflowEngine workflowEngine;
    private final SecurityManager securityManager;
    private final StreamProcessor streamProcessor;
    private final AnalyticsEngine analyticsEngine;
    
    private JTabbedPane tabbedPane;
    private String currentUser = "admin";
    
    public WeaveGUI() {
        // Initialize core components
        this.messageBus = new WeaveMessageBus();
        this.workflowEngine = new WorkflowEngine(messageBus);
        this.securityManager = new SecurityManager();
        this.streamProcessor = new StreamProcessor(messageBus);
        this.analyticsEngine = new AnalyticsEngine();
        
        // Setup GUI
        initializeGUI();
        setupEventHandlers();
    }
    
    private void initializeGUI() {
        setTitle("Weave™ Platform v1.0.0 - Enterprise Bioinformatics");
        setSize(1400, 900);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Create menu bar
        setJMenuBar(createMenuBar());
        
        // Create main tabbed pane
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.PLAIN, 14));
        
        // Add tabs for each module
        tabbedPane.addTab("🏠 Dashboard", createDashboardPanel());
        tabbedPane.addTab("🔗 Integrator", createIntegratorPanel());
        tabbedPane.addTab("⚙️ Workflow", createWorkflowPanel());
        tabbedPane.addTab("🗂️ MDM", createMDMPanel());
        tabbedPane.addTab("📡 Stream", createStreamPanel());
        tabbedPane.addTab("📊 Analytics", createAnalyticsPanel());
        tabbedPane.addTab("🔐 Security", createSecurityPanel());
        
        add(tabbedPane);
        
        // Add status bar
        add(createStatusBar(), BorderLayout.SOUTH);
    }
    
    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        // File menu
        JMenu fileMenu = new JMenu("File");
        fileMenu.add(createMenuItem("New Project", e -> showInfo("New Project")));
        fileMenu.add(createMenuItem("Open Project", e -> showInfo("Open Project")));
        fileMenu.addSeparator();
        fileMenu.add(createMenuItem("Exit", e -> System.exit(0)));
        
        // Tools menu
        JMenu toolsMenu = new JMenu("Tools");
        toolsMenu.add(createMenuItem("Settings", e -> showInfo("Settings")));
        toolsMenu.add(createMenuItem("Preferences", e -> showInfo("Preferences")));
        
        // Help menu
        JMenu helpMenu = new JMenu("Help");
        helpMenu.add(createMenuItem("Documentation", e -> showInfo("Documentation")));
        helpMenu.add(createMenuItem("About", e -> showAbout()));
        
        menuBar.add(fileMenu);
        menuBar.add(toolsMenu);
        menuBar.add(helpMenu);
        
        return menuBar;
    }
    
    private JMenuItem createMenuItem(String text, ActionListener action) {
        JMenuItem item = new JMenuItem(text);
        item.addActionListener(action);
        return item;
    }
    
    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Title
        JLabel titleLabel = new JLabel("Dashboard Overview");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Stats panel
        JPanel statsPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        statsPanel.add(createStatCard("Active Pipelines", "3", Color.BLUE));
        statsPanel.add(createStatCard("Datasets", "12", Color.GREEN));
        statsPanel.add(createStatCard("Running Jobs", "5", Color.ORANGE));
        statsPanel.add(createStatCard("System Health", "Good", Color.CYAN));
        
        panel.add(statsPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createLineBorder(color, 2));
        card.setBackground(new Color(color.getRed(), color.getGreen(), color.getBlue(), 30));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 32));
        valueLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));
        
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        
        return card;
    }
    
    private JPanel createIntegratorPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel title = new JLabel("🔗 Data Integration");
        title.setFont(new Font("Arial", Font.BOLD, 20));
        panel.add(title, BorderLayout.NORTH);
        
        // Connector list
        String[] connectors = {"File Connector", "Database Connector", "API Connector", 
                              "NCBI Connector", "ENSEMBL Connector"};
        JList<String> connectorList = new JList<>(connectors);
        connectorList.setFont(new Font("Arial", Font.PLAIN, 14));
        
        JScrollPane scrollPane = new JScrollPane(connectorList);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(createButton("Connect", e -> connectDataSource(connectorList.getSelectedValue())));
        buttonPanel.add(createButton("Import Data", e -> importData()));
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createWorkflowPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel title = new JLabel("⚙️ Workflow Engine");
        title.setFont(new Font("Arial", Font.BOLD, 20));
        panel.add(title, BorderLayout.NORTH);
        
        // Pipeline list
        DefaultTableModel model = new DefaultTableModel(
            new String[]{"Pipeline Name", "Status", "Steps", "Last Run"}, 0);
        model.addRow(new Object[]{"RNA-Seq Analysis", "Ready", "4", "2025-11-05"});
        model.addRow(new Object[]{"Variant Calling", "Ready", "3", "2025-11-04"});
        model.addRow(new Object[]{"Metagenomics", "Running", "6", "2025-11-06"});
        
        JTable table = new JTable(model);
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.setRowHeight(25);
        
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(createButton("Create Pipeline", e -> createPipeline()));
        buttonPanel.add(createButton("Execute", e -> executePipeline()));
        buttonPanel.add(createButton("Stop", e -> stopPipeline()));
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createMDMPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel title = new JLabel("🗂️ Master Data Management");
        title.setFont(new Font("Arial", Font.BOLD, 20));
        panel.add(title, BorderLayout.NORTH);
        
        // Entity counts
        JPanel statsPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        statsPanel.add(createStatCard("Samples", "15,420", new Color(100, 150, 255)));
        statsPanel.add(createStatCard("Patients", "3,856", new Color(150, 200, 100)));
        statsPanel.add(createStatCard("Experiments", "892", new Color(255, 180, 100)));
        statsPanel.add(createStatCard("References", "47", new Color(200, 100, 255)));
        
        panel.add(statsPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createStreamPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel title = new JLabel("📡 Real-Time Stream Processing");
        title.setFont(new Font("Arial", Font.BOLD, 20));
        panel.add(title, BorderLayout.NORTH);
        
        // Stream status
        JTextArea logArea = new JTextArea(20, 50);
        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        logArea.setText("Stream Processor Status: RUNNING\n\n" +
                       "[2025-11-06 10:23:45] Sequencer feed: ACTIVE\n" +
                       "[2025-11-06 10:24:12] Quality check: PASSED\n" +
                       "[2025-11-06 10:24:35] Data received: 1.2GB\n" +
                       "[2025-11-06 10:25:01] Processing batch #1543\n");
        
        JScrollPane scrollPane = new JScrollPane(logArea);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Control buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(createButton("Start Stream", e -> startStream(logArea)));
        buttonPanel.add(createButton("Pause Stream", e -> pauseStream(logArea)));
        buttonPanel.add(createButton("Clear Log", e -> logArea.setText("")));
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createAnalyticsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel title = new JLabel("📊 Analytics & Visualization");
        title.setFont(new Font("Arial", Font.BOLD, 20));
        panel.add(title, BorderLayout.NORTH);
        
        // Analysis tools
        String[] tools = {"Differential Expression Analysis", "Variant Annotation", 
                         "Gene Set Enrichment", "PCA Visualization", "Heatmap Generator"};
        JList<String> toolList = new JList<>(tools);
        toolList.setFont(new Font("Arial", Font.PLAIN, 14));
        
        JScrollPane scrollPane = new JScrollPane(toolList);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(createButton("Run Analysis", e -> runAnalysis(toolList.getSelectedValue())));
        buttonPanel.add(createButton("View Results", e -> viewResults()));
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createSecurityPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel title = new JLabel("🔐 Security & Compliance");
        title.setFont(new Font("Arial", Font.BOLD, 20));
        panel.add(title, BorderLayout.NORTH);
        
        // Audit log
        DefaultTableModel model = new DefaultTableModel(
            new String[]{"Timestamp", "User", "Action", "Status"}, 0);
        model.addRow(new Object[]{"2025-11-06 10:23", "admin", "LOGIN", "SUCCESS"});
        model.addRow(new Object[]{"2025-11-06 10:24", "admin", "EXECUTE_PIPELINE", "SUCCESS"});
        model.addRow(new Object[]{"2025-11-06 10:25", "researcher1", "DATA_ACCESS", "SUCCESS"});
        
        JTable table = new JTable(model);
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Compliance status
        JPanel statusPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        statusPanel.add(createComplianceCard("HIPAA", "Compliant"));
        statusPanel.add(createComplianceCard("GDPR", "Compliant"));
        statusPanel.add(createComplianceCard("ISO 27001", "Certified"));
        panel.add(statusPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createComplianceCard(String standard, String status) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createLineBorder(Color.GREEN, 2));
        card.setBackground(new Color(200, 255, 200));
        
        JLabel label = new JLabel("<html><center>" + standard + "<br/><b>" + status + "</b></center></html>");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        card.add(label, BorderLayout.CENTER);
        return card;
    }
    
    private JPanel createStatusBar() {
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBorder(BorderFactory.createEtchedBorder());
        
        JLabel statusLabel = new JLabel("  Ready | User: " + currentUser + " | System: Operational");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        statusBar.add(statusLabel, BorderLayout.WEST);
        
        JLabel timeLabel = new JLabel(new Date().toString() + "  ");
        timeLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        statusBar.add(timeLabel, BorderLayout.EAST);
        
        // Update time every second
        Timer timer = new Timer(1000, e -> timeLabel.setText(new Date().toString() + "  "));
        timer.start();
        
        return statusBar;
    }
    
    private JButton createButton(String text, ActionListener action) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 12));
        button.addActionListener(action);
        return button;
    }
    
    private void setupEventHandlers() {
        // Subscribe to message bus events
        messageBus.subscribe("workflow.completed", msg -> 
            SwingUtilities.invokeLater(() -> showInfo("Pipeline completed: " + msg.getPayloadValue("pipeline"))));
    }
    
    // Action handlers
    private void connectDataSource(String connector) {
        if (connector == null) {
            showWarning("Please select a connector");
            return;
        }
        showInfo("Connecting to: " + connector);
    }
    
    private void importData() {
        showInfo("Data import initiated");
    }
    
    private void createPipeline() {
        String name = JOptionPane.showInputDialog(this, "Enter pipeline name:");
        if (name != null && !name.trim().isEmpty()) {
            showInfo("Created pipeline: " + name);
        }
    }
    
    private void executePipeline() {
        showInfo("Executing pipeline...");
    }
    
    private void stopPipeline() {
        showInfo("Pipeline stopped");
    }
    
    private void startStream(JTextArea logArea) {
        logArea.append("[" + new Date() + "] Stream started\n");
        streamProcessor.start();
    }
    
    private void pauseStream(JTextArea logArea) {
        logArea.append("[" + new Date() + "] Stream paused\n");
        streamProcessor.stop();
    }
    
    private void runAnalysis(String tool) {
        if (tool == null) {
            showWarning("Please select an analysis tool");
            return;
        }
        showInfo("Running: " + tool);
    }
    
    private void viewResults() {
        showInfo("Opening results viewer...");
    }
    
    private void showInfo(String message) {
        JOptionPane.showMessageDialog(this, message, "Information", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void showWarning(String message) {
        JOptionPane.showMessageDialog(this, message, "Warning", JOptionPane.WARNING_MESSAGE);
    }
    
    private void showAbout() {
        String about = "Weave™ Platform v1.0.0\n\n" +
                      "Enterprise Bioinformatics Integration and Analytics\n\n" +
                      "© 2025 Weave Technologies";
        JOptionPane.showMessageDialog(this, about, "About", JOptionPane.INFORMATION_MESSAGE);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            WeaveGUI gui = new WeaveGUI();
            gui.setVisible(true);
        });
    }
}