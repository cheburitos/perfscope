package com.perfscope.model;

import com.perfscope.model.tables.records.CommsRecord;
import org.jooq.Record3;
import org.jooq.Result;

public class CommData {
    private final CommsRecord comm;
    private final Result<Record3<Long, Integer, Integer>> threads;
    
    public CommData(CommsRecord comm, Result<Record3<Long, Integer, Integer>> threads) {
        this.comm = comm;
        this.threads = threads;
    }
    
    public CommsRecord getComm() {
        return comm;
    }
    
    public Result<Record3<Long, Integer, Integer>> getThreads() {
        return threads;
    }
} 