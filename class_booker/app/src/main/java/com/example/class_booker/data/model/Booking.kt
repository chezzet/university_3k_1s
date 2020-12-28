package com.example.class_booker.data.model

import android.content.ContentValues
import android.database.Cursor
import com.example.class_booker.utils.convertToLocalFromString
import com.example.class_booker.utils.convertToUtcFromString
import com.example.class_booker.utils.toUTC
import com.example.class_booker.utils.toUTCString
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

data class Booking(
    val id: UUID,
    val title: String,
    val roomId: UUID,
    val userId: UUID,
    val fromTime: Date,
    val toTime: Date,
    val technicalTimestamp: Date = LocalDateTime.now().toInstant(ZoneOffset.UTC)
        .toEpochMilli().let { Date(it).toUTC() },
    val uploaded: Boolean = false,
    val deleted: Boolean = false,

    val userName: String = "",
    val roomName: String = ""
)

object BookingCompanion {
    fun fromCursorAll(query: Cursor): List<Booking> =
        List(query.count) { index ->
            query.moveToPosition(index)
            fromCursorOne(query)
        }

    fun fromCursorOne(query: Cursor): Booking = Booking(
        UUID.fromString(query.getString(query.getColumnIndex(BOOKING_ID))),
        query.getString(query.getColumnIndex(BOOKING_TITLE)),
        UUID.fromString(query.getString(query.getColumnIndex(BOOKING_ROOM_ID))),
        UUID.fromString(query.getString(query.getColumnIndex(BOOKING_USER_ID))),
        convertToLocalFromString(query.getString(query.getColumnIndex(BOOKING_FROM_TIME))),
        convertToLocalFromString(query.getString(query.getColumnIndex(BOOKING_TO_TIME))),
        convertToUtcFromString(query.getString(query.getColumnIndex(BOOKING_TIMESTAMP))),
        userName = run {
            val columnIndex = query.getColumnIndex(BOOKING_USER_NAME)
            if (columnIndex > -1) query.getString(columnIndex)
            else ""
        },
        roomName = kotlin.run {
            val columnIndex = query.getColumnIndex(BOOKING_ROOM_NAME)
            if (columnIndex > -1) query.getString(columnIndex)
            else ""
        },
        deleted = query.getInt(query.getColumnIndex(BOOKING_DELETED)) > 0
    )

    fun toContentValues(booking: Booking): ContentValues {
        val contentValues = ContentValues()
        contentValues.put(BOOKING_ID, booking.id.toString())
        contentValues.put(BOOKING_TITLE, booking.title)
        contentValues.put(BOOKING_ROOM_ID, booking.roomId.toString())
        contentValues.put(BOOKING_USER_ID, booking.userId.toString())
        contentValues.put(BOOKING_FROM_TIME, booking.fromTime.toUTCString())
        contentValues.put(BOOKING_TO_TIME, booking.toTime.toUTCString())
        contentValues.put(BOOKING_DELETED, booking.deleted)
        contentValues.put(BOOKING_TIMESTAMP, booking.technicalTimestamp.toUTCString())
        contentValues.put(BOOKING_UPLOADED, booking.uploaded)
        return contentValues
    }

    const val BOOKING_TABLE = "booking"
    const val BOOKING_ID = "_id"
    const val BOOKING_TITLE = "title"
    const val BOOKING_ROOM_ID = "roomId"
    const val BOOKING_USER_ID = "userId"
    const val BOOKING_FROM_TIME = "fromTime"
    const val BOOKING_TO_TIME = "toTime"
    const val BOOKING_DELETED = "deleted"
    const val BOOKING_TIMESTAMP = "technicalTimestamp"
    const val BOOKING_UPLOADED = "uploaded"

    const val BOOKING_ROOM_NAME = "roomName"
    const val BOOKING_USER_NAME = "userName"
}