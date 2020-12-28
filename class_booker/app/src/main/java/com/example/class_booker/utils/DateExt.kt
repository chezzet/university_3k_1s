package com.example.class_booker.utils

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

private val formatter: DateFormat
    get() = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX", Locale.US)

fun convertToLocalFromString(str: String): Date {
    formatter.calendar = Calendar.getInstance(TimeZone.getDefault())
    return formatter.parse(str)
}

fun convertToUtcFromString(str: String): Date {
    formatter.calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
    return formatter.parse(str)
}

fun Date.toFormattedString(): String {
    return formatter.format(this)
}

fun Date.toUTC(): Date {
    val instance = Calendar.getInstance()
    instance.time = this
    instance.timeZone = TimeZone.getTimeZone("UTC")
    return instance.time
}

fun Date.toUTCString(): String {
    return toUTC().toFormattedString()
}

fun LocalTime.projectFormatted(): String {
    return this.format(DateTimeFormatter.ofPattern("HH:mm"))
}