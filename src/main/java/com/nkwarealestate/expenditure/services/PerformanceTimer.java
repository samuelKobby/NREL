package com.nkwarealestate.expenditure.services;

/**
 * Utility class for measuring operation performance
 */
public class PerformanceTimer {
    
    private long startTime;
    private long endTime;
    private long memoryBefore;
    private long memoryAfter;
    private String operationName;
    private boolean isRunning;
    
    public PerformanceTimer(String operationName) {
        this.operationName = operationName;
        this.isRunning = false;
    }
    
    /**
     * Start timing an operation
     */
    public void start() {
        Runtime runtime = Runtime.getRuntime();
        this.memoryBefore = runtime.totalMemory() - runtime.freeMemory();
        this.startTime = System.nanoTime();
        this.isRunning = true;
    }
    
    /**
     * Stop timing and return the elapsed time in nanoseconds
     */
    public long stop() {
        if (!isRunning) {
            throw new IllegalStateException("Timer was not started");
        }
        
        this.endTime = System.nanoTime();
        Runtime runtime = Runtime.getRuntime();
        this.memoryAfter = runtime.totalMemory() - runtime.freeMemory();
        this.isRunning = false;
        
        return getElapsedTimeNanos();
    }
    
    /**
     * Get elapsed time in nanoseconds
     */
    public long getElapsedTimeNanos() {
        if (isRunning) {
            return System.nanoTime() - startTime;
        }
        return endTime - startTime;
    }
    
    /**
     * Get elapsed time in milliseconds
     */
    public double getElapsedTimeMs() {
        return getElapsedTimeNanos() / 1_000_000.0;
    }
    
    /**
     * Get memory usage before operation
     */
    public long getMemoryBefore() {
        return memoryBefore;
    }
    
    /**
     * Get memory usage after operation
     */
    public long getMemoryAfter() {
        return memoryAfter;
    }
    
    /**
     * Get operation name
     */
    public String getOperationName() {
        return operationName;
    }
    
    /**
     * Record this operation in the system monitor
     */
    public void recordInMonitor(SystemMonitorService monitor) {
        if (isRunning) {
            throw new IllegalStateException("Cannot record a running timer");
        }
        
        monitor.recordOperation(operationName, getElapsedTimeNanos(), 
                              memoryBefore, memoryAfter);
    }
    
    /**
     * Create a timer and automatically start it
     */
    public static PerformanceTimer startNew(String operationName) {
        PerformanceTimer timer = new PerformanceTimer(operationName);
        timer.start();
        return timer;
    }
    
    /**
     * Print timing results to console
     */
    public void printResults() {
        if (isRunning) {
            System.out.printf("[%s] Current runtime: %.2f ms\n", 
                    operationName, getElapsedTimeMs());
        } else {
            long memoryDiff = memoryAfter - memoryBefore;
            System.out.printf("[%s] Completed in %.2f ms, Memory change: %+d KB\n", 
                    operationName, getElapsedTimeMs(), memoryDiff / 1024);
        }
    }
}
