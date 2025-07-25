/*
 * This file is generated by jOOQ.
 */
package com.perfscope.model.tables.records


import com.perfscope.model.tables.MachinesView

import org.jooq.impl.TableRecordImpl


/**
 * This class is generated by jOOQ.
 */
@Suppress("UNCHECKED_CAST")
open class MachinesViewRecord() : TableRecordImpl<MachinesViewRecord>(MachinesView.MACHINES_VIEW) {

    open var id: Int?
        set(value): Unit = set(0, value)
        get(): Int? = get(0) as Int?

    open var pid: Int?
        set(value): Unit = set(1, value)
        get(): Int? = get(1) as Int?

    open var rootDir: String?
        set(value): Unit = set(2, value)
        get(): String? = get(2) as String?

    open var hostOrGuest: String?
        set(value): Unit = set(3, value)
        get(): String? = get(3) as String?

    /**
     * Create a detached, initialised MachinesViewRecord
     */
    constructor(id: Int? = null, pid: Int? = null, rootDir: String? = null, hostOrGuest: String? = null): this() {
        this.id = id
        this.pid = pid
        this.rootDir = rootDir
        this.hostOrGuest = hostOrGuest
        resetChangedOnNotNull()
    }

    /**
     * Create a detached, initialised MachinesViewRecord
     */
    constructor(value: com.perfscope.model.tables.pojos.MachinesView?): this() {
        if (value != null) {
            this.id = value.id
            this.pid = value.pid
            this.rootDir = value.rootDir
            this.hostOrGuest = value.hostOrGuest
            resetChangedOnNotNull()
        }
    }
}
