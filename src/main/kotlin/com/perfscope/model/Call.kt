package com.perfscope.model

import kotlin.time.DurationUnit.NANOSECONDS
import kotlin.time.toDuration

class Call(
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
        fun stub(name: String?): Call {
            return Call(name, null, 0L, null, null, null)
        }
    }
}