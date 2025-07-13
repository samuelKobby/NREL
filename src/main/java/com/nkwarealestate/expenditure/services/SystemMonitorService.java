package com.nkwarealestate.expenditure.services;

import com.nkwarealestate.expenditure.datastructures.CustomLinkedList;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.Duration;

/**
 * Service for monitoring system performance, memory usage, and operation timing
 */
public class SystemMonitorService {
    
    private LocalDateTime systemStartTime;
    private CustomLinkedList<OperationRecord> operationHistory;
    private long totalOperations;
    private DateTimeFormatter timeFormatter;
    
    // Inner class to record operation performance
    public static class OperationRecord {
        private String operationName;
        private long executionTimeNanos;
        private long memoryUsedBefore;
        private long memoryUsedAfter;
        private LocalDateTime timestamp;
        
        public OperationRecord(String operationName, long executionTimeNanos, 
                             long memoryUsedBefore, long memoryUsedAfter) {
            this.operationName = operationName;
            this.executionTimeNanos = executionTimeNanos;
            this.memoryUsedBefore = memoryUsedBefore;
            this.memoryUsedAfter = memoryUsedAfter;
            this.timestamp = LocalDateTime.now();
        }
        
        // Getters
        public String getOperationName() { return operationName; }
        public long getExecutionTimeNanos() { return executionTimeNanos; }
        public double getExecutionTimeMs() { return executionTimeNanos / 1_000_000.0; }
        public long getMemoryUsedBefore() { return memoryUsedBefore; }
        public long getMemoryUsedAfter() { return memoryUsedAfter; }
        public long getMemoryDifference() { return memoryUsedAfter - memoryUsedBefore; }
        public LocalDateTime getTimestamp() { return timestamp; }
    }
    
    public SystemMonitorService() {
        this.systemStartTime = LocalDateTime.now();
        this.operationHistory = new CustomLinkedList<>();
        this.totalOperations = 0;
        this.timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    }
    
    /**
     * Record an operation's performance metrics
     * 
     * @param operationName Name of the operation
     * @param executionTimeNanos Execution time in nanoseconds
     * @param memoryBefore Memory usage before operation (bytes)
     * @param memoryAfter Memory usage after operation (bytes)
     */
    public void recordOperation(String operationName, long executionTimeNanos, 
                              long memoryBefore, long memoryAfter) {
        OperationRecord record = new OperationRecord(operationName, executionTimeNanos, 
                                                    memoryBefore, memoryAfter);
        operationHistory.add(record);
        totalOperations++;
        
        // Keep only last 100 operations to prevent memory issues
        if (operationHistory.size() > 100) {
            operationHistory.remove(0);
        }
    }
    
    /**
     * Get current memory usage information
     */
    public MemoryInfo getCurrentMemoryInfo() {
        Runtime runtime = Runtime.getRuntime();
        return new MemoryInfo(runtime);
    }
    
    /**
     * Get system uptime
     */
    public Duration getUptime() {
        return Duration.between(systemStartTime, LocalDateTime.now());
    }
    
    /**
     * Get performance statistics
     */
    public PerformanceStats getPerformanceStats() {
        if (operationHistory.isEmpty()) {
            return new PerformanceStats();
        }
        
        long totalExecutionTime = 0;
        long maxExecutionTime = 0;
        long minExecutionTime = Long.MAX_VALUE;
        long totalMemoryUsed = 0;
        
        for (int i = 0; i < operationHistory.size(); i++) {
            OperationRecord record = operationHistory.get(i);
            long execTime = record.getExecutionTimeNanos();
            
            totalExecutionTime += execTime;
            maxExecutionTime = Math.max(maxExecutionTime, execTime);
            minExecutionTime = Math.min(minExecutionTime, execTime);
            totalMemoryUsed += Math.abs(record.getMemoryDifference());
        }
        
        double averageExecutionTime = (double) totalExecutionTime / operationHistory.size();
        
        return new PerformanceStats(
            operationHistory.size(),
            averageExecutionTime,
            minExecutionTime,
            maxExecutionTime,
            totalMemoryUsed
        );
    }
    
    /**
     * Get recent operations
     */
    public CustomLinkedList<OperationRecord> getRecentOperations(int count) {
        CustomLinkedList<OperationRecord> recent = new CustomLinkedList<>();
        int start = Math.max(0, operationHistory.size() - count);
        
        for (int i = start; i < operationHistory.size(); i++) {
            recent.add(operationHistory.get(i));
        }
        
        return recent;
    }
    
    /**
     * Generate comprehensive system report
     */
    public String generateSystemReport() {
        StringBuilder report = new StringBuilder();
        
        report.append("NREL SYSTEM PERFORMANCE REPORT\n");
        report.append("==============================\n\n");
        
        // System uptime
        Duration uptime = getUptime();
        report.append(String.format("System Start Time: %s\n", 
                systemStartTime.format(timeFormatter)));
        report.append(String.format("Current Time: %s\n", 
                LocalDateTime.now().format(timeFormatter)));
        report.append(String.format("Uptime: %d days, %d hours, %d minutes\n\n", 
                uptime.toDays(), uptime.toHours() % 24, uptime.toMinutes() % 60));
        
        // Memory information
        MemoryInfo memInfo = getCurrentMemoryInfo();
        report.append("MEMORY USAGE\n");
        report.append("------------\n");
        report.append(String.format("Max Memory: %d MB\n", memInfo.getMaxMemoryMB()));
        report.append(String.format("Total Allocated: %d MB\n", memInfo.getTotalMemoryMB()));
        report.append(String.format("Used Memory: %d MB (%.1f%%)\n", 
                memInfo.getUsedMemoryMB(), memInfo.getMemoryUsagePercentage()));
        report.append(String.format("Free Memory: %d MB\n", memInfo.getFreeMemoryMB()));
        report.append(String.format("Memory Efficiency: %s\n\n", 
                memInfo.getEfficiencyRating()));
        
        // Performance statistics
        PerformanceStats stats = getPerformanceStats();
        report.append("PERFORMANCE STATISTICS\n");
        report.append("----------------------\n");
        report.append(String.format("Total Operations Recorded: %d\n", stats.getTotalOperations()));
        
        if (stats.getTotalOperations() > 0) {
            report.append(String.format("Average Execution Time: %.2f ms\n", 
                    stats.getAverageExecutionTimeMs()));
            report.append(String.format("Fastest Operation: %.2f ms\n", 
                    stats.getMinExecutionTimeMs()));
            report.append(String.format("Slowest Operation: %.2f ms\n", 
                    stats.getMaxExecutionTimeMs()));
            report.append(String.format("Total Memory Allocated: %d KB\n", 
                    stats.getTotalMemoryUsed() / 1024));
        } else {
            report.append("No operations recorded yet\n");
        }
        
        // Recent operations
        CustomLinkedList<OperationRecord> recent = getRecentOperations(5);
        if (!recent.isEmpty()) {
            report.append("\nRECENT OPERATIONS (Last 5)\n");
            report.append("---------------------------\n");
            for (int i = 0; i < recent.size(); i++) {
                OperationRecord record = recent.get(i);
                report.append(String.format("%-20s: %6.2f ms, Memory: %+d KB\n",
                        record.getOperationName(),
                        record.getExecutionTimeMs(),
                        record.getMemoryDifference() / 1024));
            }
        }
        
        return report.toString();
    }
    
    /**
     * Display live performance meter
     */
    public void displayLivePerformanceMeter() {
        MemoryInfo memInfo = getCurrentMemoryInfo();
        PerformanceStats stats = getPerformanceStats();
        
        System.out.println("\n╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║                    NREL SYSTEM MONITOR                        ║");
        System.out.println("╠════════════════════════════════════════════════════════════════╣");
        
        // Memory usage meter
        System.out.printf("║ Memory Usage: %3d/%3d MB ", 
                memInfo.getUsedMemoryMB(), memInfo.getMaxMemoryMB());
        displayProgressBar(memInfo.getMemoryUsagePercentage(), 30);
        System.out.println(" ║");
        
        // System uptime
        Duration uptime = getUptime();
        System.out.printf("║ Uptime: %dd %02dh %02dm                                           ║\n",
                uptime.toDays(), uptime.toHours() % 24, uptime.toMinutes() % 60);
        
        // Performance metrics
        if (stats.getTotalOperations() > 0) {
            System.out.printf("║ Operations: %d  |  Avg Time: %.2f ms                        ║\n",
                    stats.getTotalOperations(), stats.getAverageExecutionTimeMs());
            
            // Performance rating
            String rating = getPerformanceRating(stats.getAverageExecutionTimeMs());
            System.out.printf("║ Performance: %-50s ║\n", rating);
        } else {
            System.out.println("║ Operations: 0  |  No performance data available              ║");
        }
        
        System.out.println("╚════════════════════════════════════════════════════════════════╝");
    }
    
    /**
     * Display a progress bar for visual representation
     */
    private void displayProgressBar(double percentage, int width) {
        int filled = (int) (percentage / 100.0 * width);
        System.out.print("[");
        
        for (int i = 0; i < width; i++) {
            if (i < filled) {
                if (percentage < 50) {
                    System.out.print("█");  // Green zone
                } else if (percentage < 80) {
                    System.out.print("▓");  // Yellow zone
                } else {
                    System.out.print("▒");  // Red zone
                }
            } else {
                System.out.print("░");
            }
        }
        
        System.out.printf("] %5.1f%%", percentage);
    }
    
    /**
     * Get performance rating based on average execution time
     */
    private String getPerformanceRating(double avgTimeMs) {
        if (avgTimeMs < 1) {
            return "EXCELLENT ⚡ (< 1ms)";
        } else if (avgTimeMs < 5) {
            return "GOOD 🟢 (< 5ms)";
        } else if (avgTimeMs < 20) {
            return "FAIR 🟡 (< 20ms)";
        } else {
            return "SLOW 🔴 (> 20ms)";
        }
    }
    
    // Inner classes for data organization
    public static class MemoryInfo {
        private long maxMemory;
        private long totalMemory;
        private long freeMemory;
        private long usedMemory;
        
        public MemoryInfo(Runtime runtime) {
            this.maxMemory = runtime.maxMemory();
            this.totalMemory = runtime.totalMemory();
            this.freeMemory = runtime.freeMemory();
            this.usedMemory = totalMemory - freeMemory;
        }
        
        public long getMaxMemoryMB() { return maxMemory / 1024 / 1024; }
        public long getTotalMemoryMB() { return totalMemory / 1024 / 1024; }
        public long getFreeMemoryMB() { return freeMemory / 1024 / 1024; }
        public long getUsedMemoryMB() { return usedMemory / 1024 / 1024; }
        
        public double getMemoryUsagePercentage() {
            return (double) usedMemory / totalMemory * 100;
        }
        
        public String getEfficiencyRating() {
            double usage = getMemoryUsagePercentage();
            if (usage < 50) return "EXCELLENT";
            else if (usage < 75) return "GOOD";
            else if (usage < 90) return "FAIR";
            else return "CRITICAL";
        }
    }
    
    public static class PerformanceStats {
        private int totalOperations;
        private double averageExecutionTimeNanos;
        private long minExecutionTimeNanos;
        private long maxExecutionTimeNanos;
        private long totalMemoryUsed;
        
        public PerformanceStats() {
            this.totalOperations = 0;
        }
        
        public PerformanceStats(int totalOperations, double averageExecutionTimeNanos,
                              long minExecutionTimeNanos, long maxExecutionTimeNanos,
                              long totalMemoryUsed) {
            this.totalOperations = totalOperations;
            this.averageExecutionTimeNanos = averageExecutionTimeNanos;
            this.minExecutionTimeNanos = minExecutionTimeNanos;
            this.maxExecutionTimeNanos = maxExecutionTimeNanos;
            this.totalMemoryUsed = totalMemoryUsed;
        }
        
        public int getTotalOperations() { return totalOperations; }
        public double getAverageExecutionTimeMs() { return averageExecutionTimeNanos / 1_000_000.0; }
        public double getMinExecutionTimeMs() { return minExecutionTimeNanos / 1_000_000.0; }
        public double getMaxExecutionTimeMs() { return maxExecutionTimeNanos / 1_000_000.0; }
        public long getTotalMemoryUsed() { return totalMemoryUsed; }
    }
}
