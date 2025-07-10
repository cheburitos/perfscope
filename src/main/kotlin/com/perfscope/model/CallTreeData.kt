package com.perfscope.model

import kotlin.time.DurationUnit.NANOSECONDS
import kotlin.time.toDuration

class CallTreeData(
    val name: String?,
    val totalTime: Long?,
    val callPathId: Long?,
    val timeNanos: Long?,
    val callTime: Long?,
    val returnTime: Long?
) {
    // Total time of thread
    var totalThreadTime = 1.toDuration(NANOSECONDS)

    val timeRatio: Double
        get() = timeNanos!!.toDouble() / totalThreadTime.toLong(NANOSECONDS)

    companion object {
        @JvmStatic
        fun stub(name: String?): CallTreeData {
            return CallTreeData(name, null, 0L, null, null, null)
        }
    }
}