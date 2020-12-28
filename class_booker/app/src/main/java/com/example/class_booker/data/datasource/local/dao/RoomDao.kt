package com.example.class_booker.data.datasource.local.dao

import android.database.sqlite.SQLiteDatabase.CONFLICT_IGNORE
import androidx.core.database.sqlite.transaction
import com.example.class_booker.data.datasource.local.LocalDBHelper
import com.example.class_booker.data.model.Room
import com.example.class_booker.data.model.RoomCompanion
import com.example.class_booker.data.model.RoomCompanion.ROOM_ID
import com.example.class_booker.data.model.RoomCompanion.ROOM_TABLE
import com.example.class_booker.data.model.RoomCompanion.toContentValues
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

class RoomDao(private val dbHelper: LocalDBHelper) {

    suspend fun findAll(): List<Room> = withContext(Dispatchers.IO) {
        val query = dbHelper.readableDatabase.query(ROOM_TABLE, emptyArray(), "", emptyArray(), "", "", "")
        val result: List<Room> = if (query.moveToFirst()) RoomCompanion.fromCursorAll(query) else emptyList()
        query.close()
        return@withContext result
    }

    suspend fun findById(id: UUID): Room? = withContext(Dispatchers.IO) {
        val query = dbHelper.readableDatabase.query(ROOM_TABLE, emptyArray(), "$ROOM_ID = ?", arrayOf(id.toString()), "", "", "", "1")
        val result = if (query.moveToFirst()) RoomCompanion.fromCursorOne(query) else null
        query.close()
        return@withContext result
    }

    suspend fun insertAll(rooms: List<Room>) = withContext(Dispatchers.IO) {
        dbHelper.writableDatabase.transaction(false) {
            toContentValues(rooms).forEach {
                insertWithOnConflict(ROOM_TABLE, null, it, CONFLICT_IGNORE)
            }
        }
    }
}