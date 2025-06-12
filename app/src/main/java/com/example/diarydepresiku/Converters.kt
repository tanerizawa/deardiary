package com.example.diarydepresiku

import androidx.room.TypeConverter
import java.util.Date

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun fromActivities(value: String?): List<String> {
        return value?.split("|")?.filter { it.isNotBlank() } ?: emptyList()
    }

    @TypeConverter
    fun activitiesToString(list: List<String>?): String? {
        return list?.joinToString("|")
    }
}
