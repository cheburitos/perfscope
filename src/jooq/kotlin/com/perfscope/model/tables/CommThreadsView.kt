/*
 * This file is generated by jOOQ.
 */
package com.perfscope.model.tables


import com.perfscope.model.DefaultSchema
import com.perfscope.model.tables.records.CommThreadsViewRecord

import kotlin.collections.Collection

import org.jooq.Condition
import org.jooq.Field
import org.jooq.ForeignKey
import org.jooq.InverseForeignKey
import org.jooq.Name
import org.jooq.PlainSQL
import org.jooq.QueryPart
import org.jooq.Record
import org.jooq.SQL
import org.jooq.Schema
import org.jooq.Select
import org.jooq.Stringly
import org.jooq.Table
import org.jooq.TableField
import org.jooq.TableOptions
import org.jooq.impl.DSL
import org.jooq.impl.SQLDataType
import org.jooq.impl.TableImpl


/**
 * This class is generated by jOOQ.
 */
@Suppress("UNCHECKED_CAST")
open class CommThreadsView(
    alias: Name,
    path: Table<out Record>?,
    childPath: ForeignKey<out Record, CommThreadsViewRecord>?,
    parentPath: InverseForeignKey<out Record, CommThreadsViewRecord>?,
    aliased: Table<CommThreadsViewRecord>?,
    parameters: Array<Field<*>?>?,
    where: Condition?
): TableImpl<CommThreadsViewRecord>(
    alias,
    DefaultSchema.DEFAULT_SCHEMA,
    path,
    childPath,
    parentPath,
    aliased,
    parameters,
    DSL.comment(""),
    TableOptions.view("CREATE VIEW comm_threads_view AS SELECT comm_id,(SELECT comm FROM comms WHERE id = comm_id) AS command,thread_id,(SELECT pid FROM threads WHERE id = thread_id) AS pid,(SELECT tid FROM threads WHERE id = thread_id) AS tid FROM comm_threads"),
    where,
) {
    companion object {

        /**
         * The reference instance of <code>comm_threads_view</code>
         */
        val COMM_THREADS_VIEW: CommThreadsView = CommThreadsView()
    }

    /**
     * The class holding records for this type
     */
    override fun getRecordType(): Class<CommThreadsViewRecord> = CommThreadsViewRecord::class.java

    /**
     * The column <code>comm_threads_view.comm_id</code>.
     */
    val COMM_ID: TableField<CommThreadsViewRecord, Long?> = createField(DSL.name("comm_id"), SQLDataType.BIGINT, this, "")

    /**
     * The column <code>comm_threads_view.command</code>.
     */
    val COMMAND: TableField<CommThreadsViewRecord, String?> = createField(DSL.name("command"), SQLDataType.VARCHAR(16), this, "")

    /**
     * The column <code>comm_threads_view.thread_id</code>.
     */
    val THREAD_ID: TableField<CommThreadsViewRecord, Long?> = createField(DSL.name("thread_id"), SQLDataType.BIGINT, this, "")

    /**
     * The column <code>comm_threads_view.pid</code>.
     */
    val PID: TableField<CommThreadsViewRecord, Int?> = createField(DSL.name("pid"), SQLDataType.INTEGER, this, "")

    /**
     * The column <code>comm_threads_view.tid</code>.
     */
    val TID: TableField<CommThreadsViewRecord, Int?> = createField(DSL.name("tid"), SQLDataType.INTEGER, this, "")

    private constructor(alias: Name, aliased: Table<CommThreadsViewRecord>?): this(alias, null, null, null, aliased, null, null)
    private constructor(alias: Name, aliased: Table<CommThreadsViewRecord>?, parameters: Array<Field<*>?>?): this(alias, null, null, null, aliased, parameters, null)
    private constructor(alias: Name, aliased: Table<CommThreadsViewRecord>?, where: Condition): this(alias, null, null, null, aliased, null, where)

    /**
     * Create an aliased <code>comm_threads_view</code> table reference
     */
    constructor(alias: String): this(DSL.name(alias))

    /**
     * Create an aliased <code>comm_threads_view</code> table reference
     */
    constructor(alias: Name): this(alias, null)

    /**
     * Create a <code>comm_threads_view</code> table reference
     */
    constructor(): this(DSL.name("comm_threads_view"), null)
    override fun getSchema(): Schema? = if (aliased()) null else DefaultSchema.DEFAULT_SCHEMA
    override fun `as`(alias: String): CommThreadsView = CommThreadsView(DSL.name(alias), this)
    override fun `as`(alias: Name): CommThreadsView = CommThreadsView(alias, this)
    override fun `as`(alias: Table<*>): CommThreadsView = CommThreadsView(alias.qualifiedName, this)

    /**
     * Rename this table
     */
    override fun rename(name: String): CommThreadsView = CommThreadsView(DSL.name(name), null)

    /**
     * Rename this table
     */
    override fun rename(name: Name): CommThreadsView = CommThreadsView(name, null)

    /**
     * Rename this table
     */
    override fun rename(name: Table<*>): CommThreadsView = CommThreadsView(name.qualifiedName, null)

    /**
     * Create an inline derived table from this table
     */
    override fun where(condition: Condition): CommThreadsView = CommThreadsView(qualifiedName, if (aliased()) this else null, condition)

    /**
     * Create an inline derived table from this table
     */
    override fun where(conditions: Collection<Condition>): CommThreadsView = where(DSL.and(conditions))

    /**
     * Create an inline derived table from this table
     */
    override fun where(vararg conditions: Condition): CommThreadsView = where(DSL.and(*conditions))

    /**
     * Create an inline derived table from this table
     */
    override fun where(condition: Field<Boolean?>): CommThreadsView = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(condition: SQL): CommThreadsView = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String): CommThreadsView = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String, vararg binds: Any?): CommThreadsView = where(DSL.condition(condition, *binds))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String, vararg parts: QueryPart): CommThreadsView = where(DSL.condition(condition, *parts))

    /**
     * Create an inline derived table from this table
     */
    override fun whereExists(select: Select<*>): CommThreadsView = where(DSL.exists(select))

    /**
     * Create an inline derived table from this table
     */
    override fun whereNotExists(select: Select<*>): CommThreadsView = where(DSL.notExists(select))
}
