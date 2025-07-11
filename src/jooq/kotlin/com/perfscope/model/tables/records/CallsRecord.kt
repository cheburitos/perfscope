/*
 * This file is generated by jOOQ.
 */
package com.perfscope.model.tables.records


import com.perfscope.model.tables.Calls

import org.jooq.Record1
import org.jooq.impl.UpdatableRecordImpl


/**
 * This class is generated by jOOQ.
 */
@Suppress("UNCHECKED_CAST")
open class CallsRecord() : UpdatableRecordImpl<CallsRecord>(Calls.CALLS) {

    open var id: Int?
        set(value): Unit = set(0, value)
        get(): Int? = get(0) as Int?

    open var threadId: Long?
        set(value): Unit = set(1, value)
        get(): Long? = get(1) as Long?

    open var commId: Long?
        set(value): Unit = set(2, value)
        get(): Long? = get(2) as Long?

    open var callPathId: Long?
        set(value): Unit = set(3, value)
        get(): Long? = get(3) as Long?

    open var callTime: Long?
        set(value): Unit = set(4, value)
        get(): Long? = get(4) as Long?

    open var returnTime: Long?
        set(value): Unit = set(5, value)
        get(): Long? = get(5) as Long?

    open var branchCount: Long?
        set(value): Unit = set(6, value)
        get(): Long? = get(6) as Long?

    open var callId: Long?
        set(value): Unit = set(7, value)
        get(): Long? = get(7) as Long?

    open var returnId: Long?
        set(value): Unit = set(8, value)
        get(): Long? = get(8) as Long?

    open var parentCallPathId: Long?
        set(value): Unit = set(9, value)
        get(): Long? = get(9) as Long?

    open var flags: Int?
        set(value): Unit = set(10, value)
        get(): Int? = get(10) as Int?

    open var parentId: Long?
        set(value): Unit = set(11, value)
        get(): Long? = get(11) as Long?

    open var insnCount: Long?
        set(value): Unit = set(12, value)
        get(): Long? = get(12) as Long?

    open var cycCount: Long?
        set(value): Unit = set(13, value)
        get(): Long? = get(13) as Long?

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    override fun key(): Record1<Int?> = super.key() as Record1<Int?>

    /**
     * Create a detached, initialised CallsRecord
     */
    constructor(id: Int? = null, threadId: Long? = null, commId: Long? = null, callPathId: Long? = null, callTime: Long? = null, returnTime: Long? = null, branchCount: Long? = null, callId: Long? = null, returnId: Long? = null, parentCallPathId: Long? = null, flags: Int? = null, parentId: Long? = null, insnCount: Long? = null, cycCount: Long? = null): this() {
        this.id = id
        this.threadId = threadId
        this.commId = commId
        this.callPathId = callPathId
        this.callTime = callTime
        this.returnTime = returnTime
        this.branchCount = branchCount
        this.callId = callId
        this.returnId = returnId
        this.parentCallPathId = parentCallPathId
        this.flags = flags
        this.parentId = parentId
        this.insnCount = insnCount
        this.cycCount = cycCount
        resetChangedOnNotNull()
    }

    /**
     * Create a detached, initialised CallsRecord
     */
    constructor(value: com.perfscope.model.tables.pojos.Calls?): this() {
        if (value != null) {
            this.id = value.id
            this.threadId = value.threadId
            this.commId = value.commId
            this.callPathId = value.callPathId
            this.callTime = value.callTime
            this.returnTime = value.returnTime
            this.branchCount = value.branchCount
            this.callId = value.callId
            this.returnId = value.returnId
            this.parentCallPathId = value.parentCallPathId
            this.flags = value.flags
            this.parentId = value.parentId
            this.insnCount = value.insnCount
            this.cycCount = value.cycCount
            resetChangedOnNotNull()
        }
    }
}
