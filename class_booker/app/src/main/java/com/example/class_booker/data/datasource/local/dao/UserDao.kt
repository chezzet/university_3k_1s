package com.example.class_booker.data.datasource.local.dao

import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.example.class_booker.data.datasource.local.LocalDBHelper
import com.example.class_booker.data.model.User
import com.example.class_booker.data.model.UserCompanion
import com.example.class_booker.data.model.UserCompanion.USER_ID
import com.example.class_booker.data.model.UserCompanion.USER_NAME
import com.example.class_booker.data.model.UserCompanion.USER_TABLE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

class UserDao(private val dbHelper: LocalDBHelper) {
    suspend fun findById(id: UUID): User? = withContext(Dispatchers.IO) {
        val query = try {
            dbHelper.readableDatabase.query(
                USER_TABLE,
                emptyArray(),
                "$USER_ID = ?",
                arrayOf(id.toString()),
                null,
                null,
                null,
                "1"
            )
        } catch (e: Exception) {
            return@withContext null
        }

        return@withContext if (query.moveToFirst()) {
            val value = UserCompanion.fromCursor(query)
            query.close()
            value
        } else {
            query.close()
            null
        }
    }

    suspend fun findByName(username: String): User? = withContext(Dispatchers.IO) {
        val query = try {
            dbHelper.readableDatabase.query(
                USER_TABLE,
                emptyArray(),
                "$USER_NAME = ?",
                arrayOf(username),
                null,
                null,
                null,
                "1"
            )
        } catch (e: Exception) {
            Log.i(UserDao::class.simpleName, e.toString())
            return@withContext null
        }

        return@withContext if (query.moveToFirst()) {
            val value = UserCompanion.fromCursor(query)
            query.close()
            value
        } else {
            query.close()
            null
        }
    }

    suspend fun save(user: User) = withContext(Dispatchers.IO) {
        val contentValues = UserCompanion.toContentValues(user)
        dbHelper.writableDatabase.insertWithOnConflict(USER_TABLE, null, contentValues, SQLiteDatabase.CONFLICT_IGNORE)
    }
}