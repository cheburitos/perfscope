package com.perfscope.model

import org.jooq.Record3
import org.jooq.Result

class CommandData(val id: Long, val command: String, val threads: Result<Record3<Long?, Int?, Int?>?>?)