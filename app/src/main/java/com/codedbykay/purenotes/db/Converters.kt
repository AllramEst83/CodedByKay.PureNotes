package com.codedbykay.purenotes.db

import androidx.room.TypeConverter
import java.util.Date
import java.util.UUID

class Converters {

    // Date to Long conversion
    @TypeConverter
    fun fromDate(date: Date): Long {
        return date.time
    }

    @TypeConverter
    fun toDate(time: Long): Date {
        return Date(time)
    }

    // Boolean to Int conversion
    @TypeConverter
    fun fromBoolean(done: Boolean): Int {
        return if (done) 1 else 0
    }

    @TypeConverter
    fun toBoolean(done: Int): Boolean {
        return done == 1
    }

    // UUID to String conversion
    @TypeConverter
    fun fromUUID(uuid: UUID?): String? {
        return uuid?.toString()
    }

    @TypeConverter
    fun toUUID(uuidString: String?): UUID? {
        return uuidString?.let { UUID.fromString(it) }
    }
}
