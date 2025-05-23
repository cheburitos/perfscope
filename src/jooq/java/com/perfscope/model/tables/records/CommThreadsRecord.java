/*
 * This file is generated by jOOQ.
 */
package com.perfscope.model.tables.records;


import com.perfscope.model.tables.CommThreads;

import org.jooq.Record1;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class CommThreadsRecord extends UpdatableRecordImpl<CommThreadsRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>comm_threads.id</code>.
     */
    public CommThreadsRecord setId(Integer value) {
        set(0, value);
        return this;
    }

    /**
     * Getter for <code>comm_threads.id</code>.
     */
    public Integer getId() {
        return (Integer) get(0);
    }

    /**
     * Setter for <code>comm_threads.comm_id</code>.
     */
    public CommThreadsRecord setCommId(Long value) {
        set(1, value);
        return this;
    }

    /**
     * Getter for <code>comm_threads.comm_id</code>.
     */
    public Long getCommId() {
        return (Long) get(1);
    }

    /**
     * Setter for <code>comm_threads.thread_id</code>.
     */
    public CommThreadsRecord setThreadId(Long value) {
        set(2, value);
        return this;
    }

    /**
     * Getter for <code>comm_threads.thread_id</code>.
     */
    public Long getThreadId() {
        return (Long) get(2);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<Integer> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached CommThreadsRecord
     */
    public CommThreadsRecord() {
        super(CommThreads.COMM_THREADS);
    }

    /**
     * Create a detached, initialised CommThreadsRecord
     */
    public CommThreadsRecord(Integer id, Long commId, Long threadId) {
        super(CommThreads.COMM_THREADS);

        setId(id);
        setCommId(commId);
        setThreadId(threadId);
        resetChangedOnNotNull();
    }

    /**
     * Create a detached, initialised CommThreadsRecord
     */
    public CommThreadsRecord(com.perfscope.model.tables.pojos.CommThreads value) {
        super(CommThreads.COMM_THREADS);

        if (value != null) {
            setId(value.getId());
            setCommId(value.getCommId());
            setThreadId(value.getThreadId());
            resetChangedOnNotNull();
        }
    }
}
