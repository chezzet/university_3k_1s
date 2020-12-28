package com.example.class_booker.data.model

import android.content.ContentValues
import android.database.Cursor
import com.example.class_booker.utils.toUTCString
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

data class Room(
    val id: UUID,
    val name: String,
    val description: String,
    val capacity: Int,
    val technicalTimestamp: Date = LocalDateTime.now().atZone(ZoneId.of("UTC")).toInstant()
        .toEpochMilli().let { Date(it) },
    val deleted: Boolean = false
)

object RoomCompanion {
    fun fromCursorAll(query: Cursor): List<Room> = List(query.count) { index ->
        query.moveToPosition(index)
        fromCursorOne(query)
    }

    fun fromCursorOne(query: Cursor): Room = Room(
        UUID.fromString(query.getString(query.getColumnIndex(ROOM_ID))),
        query.getString(query.getColumnIndex(ROOM_NAME)),
        query.getString(query.getColumnIndex(ROOM_DESCRIPTION)),
        query.getInt(query.getColumnIndex(ROOM_CAPACITY)),
    )

    fun toContentValues(rooms: List<Room>): List<ContentValues> = rooms.map {
        val contentValues = ContentValues()
        contentValues.put(ROOM_ID, it.id.toString())
        contentValues.put(ROOM_NAME, it.name)
        contentValues.put(ROOM_DESCRIPTION, it.description)
        contentValues.put(ROOM_CAPACITY, it.capacity)
        contentValues.put(ROOM_TIMESTAMP, it.technicalTimestamp?.toString() ?: Date().toUTCString())
        contentValues.put(ROOM_DELETED, it.deleted)
        contentValues
    }


    const val ROOM_TABLE = "room"
    const val ROOM_ID = "_id"
    const val ROOM_NAME = "nae"
    const val ROOM_DESCRIPTION = "description"
    const val ROOM_CAPACITY = "capacity"
    const val ROOM_DELETED = "deleted"
    const val ROOM_TIMESTAMP = "technicalTimestamp"
}