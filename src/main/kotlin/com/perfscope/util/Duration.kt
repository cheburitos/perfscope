package com.perfscope.util

class Duration private constructor(private val nanos: Long) {
    override fun toString(): String {
        return format(nanos)
    }

    companion object {
        private const val MICROSECOND_THRESHOLD: Long = 1000
        private const val MILLISECOND_THRESHOLD: Long = MICROSECOND_THRESHOLD * 1000
        private const val SECONDS_THRESHOLD: Long = MILLISECOND_THRESHOLD * 1000

        @JvmStatic
        fun ofNanos(nanos: Long): Duration {
            return Duration(nanos)
        }

        fun format(nanos: Long): String {
            if (nanos >= SECONDS_THRESHOLD) return formatSize(nanos, SECONDS_THRESHOLD, "s")
            if (nanos >= MILLISECOND_THRESHOLD) return formatSize(nanos, MILLISECOND_THRESHOLD, "ms")
            if (nanos >= MICROSECOND_THRESHOLD) return formatSize(nanos, MICROSECOND_THRESHOLD, "us")
            return formatSize(nanos, 1, "ns")
        }

        private fun formatSize(size: Long, divider: Long, unitName: String?): String {
            return Math.round(size.toDouble() / divider).toString() + " " + unitName
        }
    }
}
