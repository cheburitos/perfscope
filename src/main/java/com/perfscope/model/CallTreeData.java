package com.perfscope.model;

public class CallTreeData {
    private final String label;
    private final Long callPathId;
    private final Long time;
    private Long maxTime = 1L;
    
    public CallTreeData(String label, Long callPathId, Long time) {
        this.label = label;
        this.callPathId = callPathId;
        this.time = time;
    }
    
    public String getLabel() {
        return label;
    }
    
    public Long getCallPathId() {
        return callPathId;
    }
    
    public Long getTime() {
        return time;
    }
    
    public void setMaxTime(Long maxTime) {
        this.maxTime = maxTime;
    }
    
    public double getTimeRatio() {
        return (double) time / maxTime;
    }
    
    @Override
    public String toString() {
        return label;
    }
} 