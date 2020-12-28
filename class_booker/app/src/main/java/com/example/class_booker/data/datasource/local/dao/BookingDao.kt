package com.example.class_booker.data.datasource.local.dao

import android.database.sqlite.SQLiteDatabase.CONFLICT_IGNORE
import android.database.sqlite.SQLiteDatabase.CONFLICT_REPLACE
import android.util.Log
import androidx.core.database.sqlite.transaction
import com.example.class_booker.data.UserHolder
import com.example.class_booker.data.datasource.local.LocalDBHelper
import com.example.class_booker.data.model.Booking
import com.example.class_booker.data.model.BookingCompanion
import com.example.class_booker.data.model.BookingCompanion.BOOKING_DELETED
import com.example.class_booker.data.model.BookingCompanion.BOOKING_FROM_TIME
import com.example.class_booker.data.model.BookingCompanion.BOOKING_ID
import com.example.class_booker.data.model.BookingCompanion.BOOKING_ROOM_ID
import com.example.class_booker.data.model.BookingCompanion.BOOKING_ROOM_NAME
import com.example.class_booker.data.model.BookingCompanion.BOOKING_TABLE
import com.example.class_booker.data.model.BookingCompanion.BOOKING_TO_TIME
import com.example.class_booker.data.model.BookingCompanion.BOOKING_UPLOADED
import com.example.class_booker.data.model.BookingCompanion.BOOKING_USER_ID
import com.example.class_booker.data.model.BookingCompanion.BOOKING_USER_NAME
import com.example.class_booker.data.model.BookingCompanion.toContentValues
import com.example.class_booker.data.model.RoomCompanion.ROOM_ID
import com.example.class_booker.data.model.RoomCompanion.ROOM_NAME
import com.example.class_booker.data.model.RoomCompanion.ROOM_TABLE
import com.example.class_booker.data.model.UserCompanion.USER_ID
import com.example.class_booker.data.model.UserCompanion.USER_NAME
import com.example.class_booker.data.model.UserCompanion.USER_TABLE
import com.example.class_booker.utils.toUTCString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

class BookingDao(private val dbHelper: LocalDBHelper) {

    suspend fun findAllBetween(from: Date, to: Date): List<Booking> = withContext(Dispatchers.IO) {
        val query = dbHelper.readableDatabase.rawQuery(
            "SELECT $BOOKING_TABLE.*, COALESCE($ROOM_TABLE.$ROOM_NAME, 'UNKNOWN') as $BOOKING_ROOM_NAME, COALESCE($USER_TABLE.$USER_NAME, 'UNKNOWN')  as $BOOKING_USER_NAME " +
                    "FROM $BOOKING_TABLE " +
                    "LEFT JOIN $ROOM_TABLE ON $ROOM_TABLE.$ROOM_ID = $BOOKING_TABLE.$BOOKING_ROOM_ID " +
                    "LEFT JOIN $USER_TABLE ON $USER_TABLE.$USER_ID = $BOOKING_TABLE.$BOOKING_USER_ID " +
                    "WHERE ($BOOKING_FROM_TIME BETWEEN ?1 AND ?2 OR $BOOKING_TO_TIME BETWEEN ?1 AND ?2) AND $BOOKING_TABLE.$BOOKING_USER_ID = ?3 AND $BOOKING_TABLE.$BOOKING_DELETED = 0",
            arrayOf(from.toUTCString(), to.toUTCString(), UserHolder.id.toString())
        )
        val result = if (query.moveToFirst())
            BookingCompanion.fromCursorAll(query)
        else emptyList()
        query.close()
        return@withContext result
    }

    suspend fun findAllBetween(from: Date, to: Date, roomId: UUID): List<Booking> = withContext(Dispatchers.IO) {
        val query = dbHelper.readableDatabase.rawQuery(
            "SELECT $BOOKING_TABLE.*, COALESCE($ROOM_TABLE.$ROOM_NAME, 'UNKNOWN') as $BOOKING_ROOM_NAME, COALESCE($USER_TABLE.$USER_NAME, 'UNKNOWN')  as $BOOKING_USER_NAME " +
                    "FROM $BOOKING_TABLE " +
                    "LEFT JOIN $ROOM_TABLE ON $ROOM_TABLE.$ROOM_ID = $BOOKING_TABLE.$BOOKING_ROOM_ID " +
                    "LEFT JOIN $USER_TABLE ON $USER_TABLE.$USER_ID = $BOOKING_TABLE.$BOOKING_USER_ID " +
                    "WHERE ($BOOKING_FROM_TIME BETWEEN ?1 AND ?2 OR $BOOKING_TO_TIME BETWEEN ?1 AND ?2) AND $BOOKING_TABLE.$BOOKING_ROOM_ID = ?3 AND $BOOKING_TABLE.$BOOKING_DELETED = 0",
            arrayOf(from.toUTCString(), to.toUTCString(), roomId.toString())
        )
        val result = if (query.moveToFirst())
            BookingCompanion.fromCursorAll(query)
        else emptyList()
        query.close()
        return@withContext result
    }

    suspend fun findById(id: UUID): Booking? = withContext(Dispatchers.IO) {
        val query = dbHelper.readableDatabase
            .query(BOOKING_TABLE, emptyArray(), "$BOOKING_ID = ? AND $BOOKING_DELETED = 0", arrayOf(id.toString()), "", "", "", "1")
        val result = if (query.moveToFirst()) BookingCompanion.fromCursorOne(query) else null
        query.close()
        return@withContext result
    }

    suspend fun save(booking: Booking) = withContext(Dispatchers.IO) {
        val contentValues = toContentValues(booking)
        dbHelper.writableDatabase.insert(BOOKING_TABLE, null, contentValues)
    }

    suspend fun update(booking: Booking) = withContext(Dispatchers.IO) {
        val contentValues = toContentValues(booking)
        dbHelper.writableDatabase.updateWithOnConflict(BOOKING_TABLE, contentValues, "$BOOKING_ID = ?", arrayOf(booking.id.toString()), CONFLICT_REPLACE)
    }

    suspend fun findAllByUserNotUploaded(userId: UUID): List<Booking> = withContext(Dispatchers.IO) {
        val query = dbHelper.readableDatabase.query(
            BOOKING_TABLE,
            emptyArray(),
            "$BOOKING_USER_ID = ? AND $BOOKING_UPLOADED = 0",
            arrayOf(userId.toString()),
            "",
            "",
            ""
        )
        val result = if (query.moveToFirst())
            BookingCompanion.fromCursorAll(query)
        else emptyList()
        query.close()
        return@withContext result
    }

    suspend fun insertAll(bookings: List<Booking>) = withContext(Dispatchers.IO) {
        dbHelper.writableDatabase.transaction(exclusive = false) {
            bookings.forEach { booking ->
                kotlin.runCatching {
                    insertOrThrow(
                        BOOKING_TABLE,
                        null,
                        toContentValues(booking)
                    )
                }.onFailure {
                    Log.w(BookingDao::class.simpleName, "Error while inserting updated: ", it)
                    updateWithOnConflict(BOOKING_TABLE, toContentValues(booking), "$BOOKING_ID = ?", arrayOf(booking.id.toString()), CONFLICT_IGNORE)
                }
            }
        }
    }
}