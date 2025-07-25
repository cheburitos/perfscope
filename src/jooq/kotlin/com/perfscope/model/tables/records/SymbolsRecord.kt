/*
 * This file is generated by jOOQ.
 */
package com.perfscope.model.tables.records


import com.perfscope.model.tables.Symbols

import org.jooq.Record1
import org.jooq.impl.UpdatableRecordImpl


/**
 * This class is generated by jOOQ.
 */
@Suppress("UNCHECKED_CAST")
open class SymbolsRecord() : UpdatableRecordImpl<SymbolsRecord>(Symbols.SYMBOLS) {

    open var id: Int?
        set(value): Unit = set(0, value)
        get(): Int? = get(0) as Int?

    open var dsoId: Long?
        set(value): Unit = set(1, value)
        get(): Long? = get(1) as Long?

    open var symStart: Long?
        set(value): Unit = set(2, value)
        get(): Long? = get(2) as Long?

    open var symEnd: Long?
        set(value): Unit = set(3, value)
        get(): Long? = get(3) as Long?

    open var binding: Int?
        set(value): Unit = set(4, value)
        get(): Int? = get(4) as Int?

    open var name: String?
        set(value): Unit = set(5, value)
        get(): String? = get(5) as String?

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    override fun key(): Record1<Int?> = super.key() as Record1<Int?>

    /**
     * Create a detached, initialised SymbolsRecord
     */
    constructor(id: Int? = null, dsoId: Long? = null, symStart: Long? = null, symEnd: Long? = null, binding: Int? = null, name: String? = null): this() {
        this.id = id
        this.dsoId = dsoId
        this.symStart = symStart
        this.symEnd = symEnd
        this.binding = binding
        this.name = name
        resetChangedOnNotNull()
    }

    /**
     * Create a detached, initialised SymbolsRecord
     */
    constructor(value: com.perfscope.model.tables.pojos.Symbols?): this() {
        if (value != null) {
            this.id = value.id
            this.dsoId = value.dsoId
            this.symStart = value.symStart
            this.symEnd = value.symEnd
            this.binding = value.binding
            this.name = value.name
            resetChangedOnNotNull()
        }
    }
}
