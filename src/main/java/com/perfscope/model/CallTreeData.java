package com.perfscope.model;

public class CallTreeData {
    private final String name;
    private final Long count;
    private final Long totalTime;
    private final Long callPathId;
    private final Long timeNanos;
    private Long maxTime = 1L;
    
    public CallTreeData(String name, Long count, Long totalTime, Long callPathId, Long timeNanos) {
        this.name = name;
        this.count = count;
        this.totalTime = totalTime;
        this.callPathId = callPathId;
        this.timeNanos = timeNanos;
    }

    public String getName() {
        return name;
    }

    public Long getCount() {
        return count;
    }

    public Long getTotalTime() {
        return totalTime;
    }

    public Long getCallPathId() {
        return callPathId;
    }
    
    public Long getTimeNanos() {
        return timeNanos;
    }
    
    public void setMaxTime(Long maxTime) {
        this.maxTime = maxTime;
    }
    
    public double getTimeRatio() {
        return (double) timeNanos / maxTime;
    }
} 