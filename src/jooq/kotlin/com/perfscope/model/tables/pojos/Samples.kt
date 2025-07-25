/*
 * This file is generated by jOOQ.
 */
package com.perfscope.model.tables.pojos


import java.io.Serializable


/**
 * This class is generated by jOOQ.
 */
@Suppress("UNCHECKED_CAST")
data class Samples(
    val id: Int? = null,
    val evselId: Long? = null,
    val machineId: Long? = null,
    val threadId: Long? = null,
    val commId: Long? = null,
    val dsoId: Long? = null,
    val symbolId: Long? = null,
    val symOffset: Long? = null,
    val ip: Long? = null,
    val time: Long? = null,
    val cpu: Int? = null,
    val toDsoId: Long? = null,
    val toSymbolId: Long? = null,
    val toSymOffset: Long? = null,
    val toIp: Long? = null,
    val branchType: Int? = null,
    val inTx: Boolean? = null,
    val callPathId: Long? = null,
    val insnCount: Long? = null,
    val cycCount: Long? = null,
    val flags: Int? = null
): Serializable {

    override fun equals(other: Any?): Boolean {
        if (this === other)
            return true
        if (other == null)
            return false
        if (this::class != other::class)
            return false
        val o: Samples = other as Samples
        if (this.id == null) {
            if (o.id != null)
                return false
        }
        else if (this.id != o.id)
            return false
        if (this.evselId == null) {
            if (o.evselId != null)
                return false
        }
        else if (this.evselId != o.evselId)
            return false
        if (this.machineId == null) {
            if (o.machineId != null)
                return false
        }
        else if (this.machineId != o.machineId)
            return false
        if (this.threadId == null) {
            if (o.threadId != null)
                return false
        }
        else if (this.threadId != o.threadId)
            return false
        if (this.commId == null) {
            if (o.commId != null)
                return false
        }
        else if (this.commId != o.commId)
            return false
        if (this.dsoId == null) {
            if (o.dsoId != null)
                return false
        }
        else if (this.dsoId != o.dsoId)
            return false
        if (this.symbolId == null) {
            if (o.symbolId != null)
                return false
        }
        else if (this.symbolId != o.symbolId)
            return false
        if (this.symOffset == null) {
            if (o.symOffset != null)
                return false
        }
        else if (this.symOffset != o.symOffset)
            return false
        if (this.ip == null) {
            if (o.ip != null)
                return false
        }
        else if (this.ip != o.ip)
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
        if (this.toDsoId == null) {
            if (o.toDsoId != null)
                return false
        }
        else if (this.toDsoId != o.toDsoId)
            return false
        if (this.toSymbolId == null) {
            if (o.toSymbolId != null)
                return false
        }
        else if (this.toSymbolId != o.toSymbolId)
            return false
        if (this.toSymOffset == null) {
            if (o.toSymOffset != null)
                return false
        }
        else if (this.toSymOffset != o.toSymOffset)
            return false
        if (this.toIp == null) {
            if (o.toIp != null)
                return false
        }
        else if (this.toIp != o.toIp)
            return false
        if (this.branchType == null) {
            if (o.branchType != null)
                return false
        }
        else if (this.branchType != o.branchType)
            return false
        if (this.inTx == null) {
            if (o.inTx != null)
                return false
        }
        else if (this.inTx != o.inTx)
            return false
        if (this.callPathId == null) {
            if (o.callPathId != null)
                return false
        }
        else if (this.callPathId != o.callPathId)
            return false
        if (this.insnCount == null) {
            if (o.insnCount != null)
                return false
        }
        else if (this.insnCount != o.insnCount)
            return false
        if (this.cycCount == null) {
            if (o.cycCount != null)
                return false
        }
        else if (this.cycCount != o.cycCount)
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
        result = prime * result + (if (this.evselId == null) 0 else this.evselId.hashCode())
        result = prime * result + (if (this.machineId == null) 0 else this.machineId.hashCode())
        result = prime * result + (if (this.threadId == null) 0 else this.threadId.hashCode())
        result = prime * result + (if (this.commId == null) 0 else this.commId.hashCode())
        result = prime * result + (if (this.dsoId == null) 0 else this.dsoId.hashCode())
        result = prime * result + (if (this.symbolId == null) 0 else this.symbolId.hashCode())
        result = prime * result + (if (this.symOffset == null) 0 else this.symOffset.hashCode())
        result = prime * result + (if (this.ip == null) 0 else this.ip.hashCode())
        result = prime * result + (if (this.time == null) 0 else this.time.hashCode())
        result = prime * result + (if (this.cpu == null) 0 else this.cpu.hashCode())
        result = prime * result + (if (this.toDsoId == null) 0 else this.toDsoId.hashCode())
        result = prime * result + (if (this.toSymbolId == null) 0 else this.toSymbolId.hashCode())
        result = prime * result + (if (this.toSymOffset == null) 0 else this.toSymOffset.hashCode())
        result = prime * result + (if (this.toIp == null) 0 else this.toIp.hashCode())
        result = prime * result + (if (this.branchType == null) 0 else this.branchType.hashCode())
        result = prime * result + (if (this.inTx == null) 0 else this.inTx.hashCode())
        result = prime * result + (if (this.callPathId == null) 0 else this.callPathId.hashCode())
        result = prime * result + (if (this.insnCount == null) 0 else this.insnCount.hashCode())
        result = prime * result + (if (this.cycCount == null) 0 else this.cycCount.hashCode())
        result = prime * result + (if (this.flags == null) 0 else this.flags.hashCode())
        return result
    }

    override fun toString(): String {
        val sb = StringBuilder("Samples (")

        sb.append(id)
        sb.append(", ").append(evselId)
        sb.append(", ").append(machineId)
        sb.append(", ").append(threadId)
        sb.append(", ").append(commId)
        sb.append(", ").append(dsoId)
        sb.append(", ").append(symbolId)
        sb.append(", ").append(symOffset)
        sb.append(", ").append(ip)
        sb.append(", ").append(time)
        sb.append(", ").append(cpu)
        sb.append(", ").append(toDsoId)
        sb.append(", ").append(toSymbolId)
        sb.append(", ").append(toSymOffset)
        sb.append(", ").append(toIp)
        sb.append(", ").append(branchType)
        sb.append(", ").append(inTx)
        sb.append(", ").append(callPathId)
        sb.append(", ").append(insnCount)
        sb.append(", ").append(cycCount)
        sb.append(", ").append(flags)

        sb.append(")")
        return sb.toString()
    }
}
