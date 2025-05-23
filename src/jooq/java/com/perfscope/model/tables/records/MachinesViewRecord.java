/*
 * This file is generated by jOOQ.
 */
package com.perfscope.model.tables.records;


import com.perfscope.model.tables.MachinesView;

import org.jooq.impl.TableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class MachinesViewRecord extends TableRecordImpl<MachinesViewRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>machines_view.id</code>.
     */
    public MachinesViewRecord setId(Integer value) {
        set(0, value);
        return this;
    }

    /**
     * Getter for <code>machines_view.id</code>.
     */
    public Integer getId() {
        return (Integer) get(0);
    }

    /**
     * Setter for <code>machines_view.pid</code>.
     */
    public MachinesViewRecord setPid(Integer value) {
        set(1, value);
        return this;
    }

    /**
     * Getter for <code>machines_view.pid</code>.
     */
    public Integer getPid() {
        return (Integer) get(1);
    }

    /**
     * Setter for <code>machines_view.root_dir</code>.
     */
    public MachinesViewRecord setRootDir(String value) {
        set(2, value);
        return this;
    }

    /**
     * Getter for <code>machines_view.root_dir</code>.
     */
    public String getRootDir() {
        return (String) get(2);
    }

    /**
     * Setter for <code>machines_view.host_or_guest</code>.
     */
    public MachinesViewRecord setHostOrGuest(String value) {
        set(3, value);
        return this;
    }

    /**
     * Getter for <code>machines_view.host_or_guest</code>.
     */
    public String getHostOrGuest() {
        return (String) get(3);
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached MachinesViewRecord
     */
    public MachinesViewRecord() {
        super(MachinesView.MACHINES_VIEW);
    }

    /**
     * Create a detached, initialised MachinesViewRecord
     */
    public MachinesViewRecord(Integer id, Integer pid, String rootDir, String hostOrGuest) {
        super(MachinesView.MACHINES_VIEW);

        setId(id);
        setPid(pid);
        setRootDir(rootDir);
        setHostOrGuest(hostOrGuest);
        resetChangedOnNotNull();
    }

    /**
     * Create a detached, initialised MachinesViewRecord
     */
    public MachinesViewRecord(com.perfscope.model.tables.pojos.MachinesView value) {
        super(MachinesView.MACHINES_VIEW);

        if (value != null) {
            setId(value.getId());
            setPid(value.getPid());
            setRootDir(value.getRootDir());
            setHostOrGuest(value.getHostOrGuest());
            resetChangedOnNotNull();
        }
    }
}
