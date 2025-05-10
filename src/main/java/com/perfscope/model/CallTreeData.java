package com.perfscope.model;

public class CallTreeData {
    private final String name;
    private final Long totalTime;
    private final Long callPathId;
    private final Long timeNanos;
    private final Long callTime;
    private final Long returnTime;
    private Long totalTimeNanos = 1L;
    
    public CallTreeData(String name, Long totalTime, Long callPathId, Long timeNanos, Long callTime, Long returnTime) {
        this.name = name;
        this.totalTime = totalTime;
        this.callPathId = callPathId;
        this.timeNanos = timeNanos;
        this.callTime = callTime;
        this.returnTime = returnTime;
    }

    public String getName() {
        return name;
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
    
    public void setTotalTimeNanos(Long totalTimeNanos) {
        this.totalTimeNanos = totalTimeNanos;
    }
    
    public double getTimeRatio() {
        return (double) timeNanos / totalTimeNanos;
    }

    public Long getCallTime() {
        return callTime;
    }

    public Long getReturnTime() {
        return returnTime;
    }

    public static CallTreeData stub(String name) {
        return new CallTreeData(name, null, 0L, null, null, null);
    }
} 