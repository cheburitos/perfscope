/*
 * This file is generated by jOOQ.
 */
package com.perfscope.model.tables.records


import com.perfscope.model.tables.ContextSwitches

import org.jooq.Record1
import org.jooq.impl.UpdatableRecordImpl


/**
 * This class is generated by jOOQ.
 */
@Suppress("UNCHECKED_CAST")
open class ContextSwitchesRecord() : UpdatableRecordImpl<ContextSwitchesRecord>(ContextSwitches.CONTEXT_SWITCHES) {

    open var id: Int?
        set(value): Unit = set(0, value)
        get(): Int? = get(0) as Int?

    open var machineId: Long?
        set(value): Unit = set(1, value)
        get(): Long? = get(1) as Long?

    open var time: Long?
        set(value): Unit = set(2, value)
        get(): Long? = get(2) as Long?

    open var cpu: Int?
        set(value): Unit = set(3, value)
        get(): Int? = get(3) as Int?

    open var threadOutId: Long?
        set(value): Unit = set(4, value)
        get(): Long? = get(4) as Long?

    open var commOutId: Long?
        set(value): Unit = set(5, value)
        get(): Long? = get(5) as Long?

    open var threadInId: Long?
        set(value): Unit = set(6, value)
        get(): Long? = get(6) as Long?

    open var commInId: Long?
        set(value): Unit = set(7, value)
        get(): Long? = get(7) as Long?

    open var flags: Int?
        set(value): Unit = set(8, value)
        get(): Int? = get(8) as Int?

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    override fun key(): Record1<Int?> = super.key() as Record1<Int?>

    /**
     * Create a detached, initialised ContextSwitchesRecord
     */
    constructor(id: Int? = null, machineId: Long? = null, time: Long? = null, cpu: Int? = null, threadOutId: Long? = null, commOutId: Long? = null, threadInId: Long? = null, commInId: Long? = null, flags: Int? = null): this() {
        this.id = id
        this.machineId = machineId
        this.time = time
        this.cpu = cpu
        this.threadOutId = threadOutId
        this.commOutId = commOutId
        this.threadInId = threadInId
        this.commInId = commInId
        this.flags = flags
        resetChangedOnNotNull()
    }

    /**
     * Create a detached, initialised ContextSwitchesRecord
     */
    constructor(value: com.perfscope.model.tables.pojos.ContextSwitches?): this() {
        if (value != null) {
            this.id = value.id
            this.machineId = value.machineId
            this.time = value.time
            this.cpu = value.cpu
            this.threadOutId = value.threadOutId
            this.commOutId = value.commOutId
            this.threadInId = value.threadInId
            this.commInId = value.commInId
            this.flags = value.flags
            resetChangedOnNotNull()
        }
    }
}
