/*
 * This file is generated by jOOQ.
 */
package com.perfscope.model.tables.pojos


import java.io.Serializable


/**
 * This class is generated by jOOQ.
 */
@Suppress("UNCHECKED_CAST")
data class Dsos(
    val id: Int? = null,
    val machineId: Long? = null,
    val shortName: String? = null,
    val longName: String? = null,
    val buildId: String? = null
): Serializable {

    override fun equals(other: Any?): Boolean {
        if (this === other)
            return true
        if (other == null)
            return false
        if (this::class != other::class)
            return false
        val o: Dsos = other as Dsos
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
        if (this.shortName == null) {
            if (o.shortName != null)
                return false
        }
        else if (this.shortName != o.shortName)
            return false
        if (this.longName == null) {
            if (o.longName != null)
                return false
        }
        else if (this.longName != o.longName)
            return false
        if (this.buildId == null) {
            if (o.buildId != null)
                return false
        }
        else if (this.buildId != o.buildId)
            return false
        return true
    }

    override fun hashCode(): Int {
        val prime = 31
        var result = 1
        result = prime * result + (if (this.id == null) 0 else this.id.hashCode())
        result = prime * result + (if (this.machineId == null) 0 else this.machineId.hashCode())
        result = prime * result + (if (this.shortName == null) 0 else this.shortName.hashCode())
        result = prime * result + (if (this.longName == null) 0 else this.longName.hashCode())
        result = prime * result + (if (this.buildId == null) 0 else this.buildId.hashCode())
        return result
    }

    override fun toString(): String {
        val sb = StringBuilder("Dsos (")

        sb.append(id)
        sb.append(", ").append(machineId)
        sb.append(", ").append(shortName)
        sb.append(", ").append(longName)
        sb.append(", ").append(buildId)

        sb.append(")")
        return sb.toString()
    }
}
