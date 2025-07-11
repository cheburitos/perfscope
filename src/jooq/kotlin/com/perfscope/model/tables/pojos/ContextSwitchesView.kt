/*
 * This file is generated by jOOQ.
 */
package com.perfscope.model.tables.pojos


import java.io.Serializable


/**
 * This class is generated by jOOQ.
 */
@Suppress("UNCHECKED_CAST")
data class ContextSwitchesView(
    val id: Int? = null,
    val machineId: Long? = null,
    val time: Long? = null,
    val cpu: Int? = null,
    val pidOut: Int? = null,
    val tidOut: Int? = null,
    val commOut: String? = null,
    val pidIn: Int? = null,
    val tidIn: Int? = null,
    val commIn: String? = null,
    val flags: String? = null
): Serializable {

    override fun equals(other: Any?): Boolean {
        if (this === other)
            return true
        if (other == null)
            return false
        if (this::class != other::class)
            return false
        val o: ContextSwitchesView = other as ContextSwitchesView
        if (this.id == null) {
            if (o.id != null)
                return false
        }
        else if (this.id != o.id)
            return false
        if (this.machineId == null) {
            if (o.machineId != null)
                return false
        }
        else if (this.machineId != o.machineId)
            return false
        if (this.time == null) {
            if (o.time != null)
                return false
        }
        else if (this.time != o.time)
            return false
        if (this.cpu == null) {
            if (o.cpu != null)
                return false
        }
        else if (this.cpu != o.cpu)
            return false
        if (this.pidOut == null) {
            if (o.pidOut != null)
                return false
        }
        else if (this.pidOut != o.pidOut)
            return false
        if (this.tidOut == null) {
            if (o.tidOut != null)
                return false
        }
        else if (this.tidOut != o.tidOut)
            return false
        if (this.commOut == null) {
            if (o.commOut != null)
                return false
        }
        else if (this.commOut != o.commOut)
            return false
        if (this.pidIn == null) {
            if (o.pidIn != null)
                return false
        }
        else if (this.pidIn != o.pidIn)
            return false
        if (this.tidIn == null) {
            if (o.tidIn != null)
                return false
        }
        else if (this.tidIn != o.tidIn)
            return false
        if (this.commIn == null) {
            if (o.commIn != null)
                return false
        }
        else if (this.commIn != o.commIn)
            return false
        if (this.flags == null) {
            if (o.flags != null)
                return false
        }
        else if (this.flags != o.flags)
            return false
        return true
    }

    override fun hashCode(): Int {
        val prime = 31
        var result = 1
        result = prime * result + (if (this.id == null) 0 else this.id.hashCode())
        result = prime * result + (if (this.machineId == null) 0 else this.machineId.hashCode())
        result = prime * result + (if (this.time == null) 0 else this.time.hashCode())
        result = prime * result + (if (this.cpu == null) 0 else this.cpu.hashCode())
        result = prime * result + (if (this.pidOut == null) 0 else this.pidOut.hashCode())
        result = prime * result + (if (this.tidOut == null) 0 else this.tidOut.hashCode())
        result = prime * result + (if (this.commOut == null) 0 else this.commOut.hashCode())
        result = prime * result + (if (this.pidIn == null) 0 else this.pidIn.hashCode())
        result = prime * result + (if (this.tidIn == null) 0 else this.tidIn.hashCode())
        result = prime * result + (if (this.commIn == null) 0 else this.commIn.hashCode())
        result = prime * result + (if (this.flags == null) 0 else this.flags.hashCode())
        return result
    }

    override fun toString(): String {
        val sb = StringBuilder("ContextSwitchesView (")

        sb.append(id)
        sb.append(", ").append(machineId)
        sb.append(", ").append(time)
        sb.append(", ").append(cpu)
        sb.append(", ").append(pidOut)
        sb.append(", ").append(tidOut)
        sb.append(", ").append(commOut)
        sb.append(", ").append(pidIn)
        sb.append(", ").append(tidIn)
        sb.append(", ").append(commIn)
        sb.append(", ").append(flags)

        sb.append(")")
        return sb.toString()
    }
}
