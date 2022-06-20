package com.baimsg.data.db.converter

import androidx.room.TypeConverter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

/**
 * Create by Baimsg on 2022/4/1
 *
 **/
object BaseTypeConverters {

    private val localDateFormat = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    @TypeConverter
    @JvmStatic
    fun toLocalDateTime(value: String): LocalDateTime = when (value.isBlank()) {
        true -> LocalDateTime.now()
        else -> LocalDateTime.parse(value, localDateFormat)
    }

    @TypeConverter
    @JvmStatic
    fun fromLocalDateTime(value: LocalDateTime): String = localDateFormat.format(value)

    @TypeConverter
    fun toDate(timestamp: Long): Date = Date(timestamp)

    @TypeConverter
    fun toTimestamp(date: Date): Long = date.time

}
