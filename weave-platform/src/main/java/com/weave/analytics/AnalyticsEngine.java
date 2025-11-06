// FILE: src/main/java/com/weave/analytics/AnalyticsEngine.java

package com.weave.analytics;

import com.weave.model.DataSet;
import java.util.*;

/**
 * Analytics Engine - Statistical analysis and insights
 */
public class AnalyticsEngine {
    
    public AnalysisResult performStatistics(DataSet dataset, String columnName) {
        List<Object> values = dataset.getColumn(columnName);
        List<Double> numericValues = convertToNumeric(values);
        
        if (numericValues.isEmpty()) {
            return new AnalysisResult("ERROR", "No numeric values found");
        }
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("count", numericValues.size());
        stats.put("mean", calculateMean(numericValues));
        stats.put("median", calculateMedian(numericValues));
        stats.put("min", Collections.min(numericValues));
        stats.put("max", Collections.max(numericValues));
        stats.put("stddev", calculateStdDev(numericValues));
        
        return new AnalysisResult("SUCCESS", stats);
    }
    
    public DataSet filter(DataSet dataset, String column, Object value) {
        return dataset.filter(row -> value.equals(row.get(column)));
    }
    
    public Map<Object, Long> groupBy(DataSet dataset, String columnName) {
        Map<Object, Long> counts = new HashMap<>();
        for (Object value : dataset.getColumn(columnName)) {
            counts.put(value, counts.getOrDefault(value, 0L) + 1);
        }
        return counts;
    }
    
    private List<Double> convertToNumeric(List<Object> values) {
        List<Double> result = new ArrayList<>();
        for (Object value : values) {
            try {
                if (value instanceof Number) {
                    result.add(((Number) value).doubleValue());
                } else if (value != null) {
                    result.add(Double.parseDouble(value.toString()));
                }
            } catch (NumberFormatException e) {
                // Skip non-numeric values
            }
        }
        return result;
    }
    
    private double calculateMean(List<Double> values) {
        return values.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
    }
    
    private double calculateMedian(List<Double> values) {
        List<Double> sorted = new ArrayList<>(values);
        Collections.sort(sorted);
        int size = sorted.size();
        if (size % 2 == 0) {
            return (sorted.get(size/2 - 1) + sorted.get(size/2)) / 2.0;
        } else {
            return sorted.get(size/2);
        }
    }
    
    private double calculateStdDev(List<Double> values) {
        double mean = calculateMean(values);
        double variance = values.stream()
            .mapToDouble(v -> Math.pow(v - mean, 2))
            .average()
            .orElse(0.0);
        return Math.sqrt(variance);
    }
}

class AnalysisResult {
    private final String status;
    private final Object result;
    
    public AnalysisResult(String status, Object result) {
        this.status = status;
        this.result = result;
    }
    
    public String getStatus() { return status; }
    public Object getResult() { return result; }
    
    @Override
    public String toString() {
        return "AnalysisResult{status='" + status + "', result=" + result + "}";
    }
}