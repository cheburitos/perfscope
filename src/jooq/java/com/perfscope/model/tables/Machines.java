/*
 * This file is generated by jOOQ.
 */
package com.perfscope.model.tables;


import com.perfscope.model.DefaultSchema;
import com.perfscope.model.Keys;
import com.perfscope.model.tables.records.MachinesRecord;

import java.util.Collection;

import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Name;
import org.jooq.PlainSQL;
import org.jooq.QueryPart;
import org.jooq.SQL;
import org.jooq.Schema;
import org.jooq.Select;
import org.jooq.Stringly;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Machines extends TableImpl<MachinesRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>machines</code>
     */
    public static final Machines MACHINES = new Machines();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<MachinesRecord> getRecordType() {
        return MachinesRecord.class;
    }

    /**
     * The column <code>machines.id</code>.
     */
    public final TableField<MachinesRecord, Integer> ID = createField(DSL.name("id"), SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>machines.pid</code>.
     */
    public final TableField<MachinesRecord, Integer> PID = createField(DSL.name("pid"), SQLDataType.INTEGER, this, "");

    /**
     * The column <code>machines.root_dir</code>.
     */
    public final TableField<MachinesRecord, String> ROOT_DIR = createField(DSL.name("root_dir"), SQLDataType.VARCHAR(4096), this, "");

    private Machines(Name alias, Table<MachinesRecord> aliased) {
        this(alias, aliased, (Field<?>[]) null, null);
    }

    private Machines(Name alias, Table<MachinesRecord> aliased, Field<?>[] parameters, Condition where) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table(), where);
    }

    /**
     * Create an aliased <code>machines</code> table reference
     */
    public Machines(String alias) {
        this(DSL.name(alias), MACHINES);
    }

    /**
     * Create an aliased <code>machines</code> table reference
     */
    public Machines(Name alias) {
        this(alias, MACHINES);
    }

    /**
     * Create a <code>machines</code> table reference
     */
    public Machines() {
        this(DSL.name("machines"), null);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : DefaultSchema.DEFAULT_SCHEMA;
    }

    @Override
    public UniqueKey<MachinesRecord> getPrimaryKey() {
        return Keys.MACHINES__PK_MACHINES;
    }

    @Override
    public Machines as(String alias) {
        return new Machines(DSL.name(alias), this);
    }

    @Override
    public Machines as(Name alias) {
        return new Machines(alias, this);
    }

    @Override
    public Machines as(Table<?> alias) {
        return new Machines(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public Machines rename(String name) {
        return new Machines(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Machines rename(Name name) {
        return new Machines(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public Machines rename(Table<?> name) {
        return new Machines(name.getQualifiedName(), null);
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public Machines where(Condition condition) {
        return new Machines(getQualifiedName(), aliased() ? this : null, null, condition);
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public Machines where(Collection<? extends Condition> conditions) {
        return where(DSL.and(conditions));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public Machines where(Condition... conditions) {
        return where(DSL.and(conditions));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public Machines where(Field<Boolean> condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public Machines where(SQL condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public Machines where(@Stringly.SQL String condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public Machines where(@Stringly.SQL String condition, Object... binds) {
        return where(DSL.condition(condition, binds));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public Machines where(@Stringly.SQL String condition, QueryPart... parts) {
        return where(DSL.condition(condition, parts));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public Machines whereExists(Select<?> select) {
        return where(DSL.exists(select));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public Machines whereNotExists(Select<?> select) {
        return where(DSL.notExists(select));
    }
}
