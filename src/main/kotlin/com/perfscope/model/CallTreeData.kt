package com.perfscope.model

class CallTreeData(
    val name: String?,
    val totalTime: Long?,
    val callPathId: Long?,
    val timeNanos: Long?,
    val callTime: Long?,
    val returnTime: Long?
) {
    var totalTimeNanos = 1L

    val timeRatio: Double
        get() = timeNanos!!.toDouble() / totalTimeNanos

    companion object {
        @JvmStatic
        fun stub(name: String?): CallTreeData {
            return CallTreeData(name, null, 0L, null, null, null)
        }
    }
}