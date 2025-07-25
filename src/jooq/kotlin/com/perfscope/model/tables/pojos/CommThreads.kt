/*
 * This file is generated by jOOQ.
 */
package com.perfscope.model.tables.pojos


import java.io.Serializable


/**
 * This class is generated by jOOQ.
 */
@Suppress("UNCHECKED_CAST")
data class CommThreads(
    val id: Int? = null,
    val commId: Long? = null,
    val threadId: Long? = null
): Serializable {

    override fun equals(other: Any?): Boolean {
        if (this === other)
            return true
        if (other == null)
            return false
        if (this::class != other::class)
            return false
        val o: CommThreads = other as CommThreads
        if (this.id == null) {
            if (o.id != null)
                return false
        }
        else if (this.id != o.id)
            return false
        if (this.commId == null) {
            if (o.commId != null)
                return false
        }
        else if (this.commId != o.commId)
            return false
        if (this.threadId == null) {
            if (o.threadId != null)
                return false
        }
        else if (this.threadId != o.threadId)
            return false
        return true
    }

    override fun hashCode(): Int {
        val prime = 31
        var result = 1
        result = prime * result + (if (this.id == null) 0 else this.id.hashCode())
        result = prime * result + (if (this.commId == null) 0 else this.commId.hashCode())
        result = prime * result + (if (this.threadId == null) 0 else this.threadId.hashCode())
        return result
    }

    override fun toString(): String {
        val sb = StringBuilder("CommThreads (")

        sb.append(id)
        sb.append(", ").append(commId)
        sb.append(", ").append(threadId)

        sb.append(")")
        return sb.toString()
    }
}
