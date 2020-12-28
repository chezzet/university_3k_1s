package com.example.class_booker.data.model

import android.content.ContentValues
import android.database.Cursor
import java.util.*

data class User(
    val id: UUID,
    val name: String,
    val role: Int
)

object UserCompanion {
    fun fromCursor(query: Cursor): User {
        return User(
            UUID.fromString(query.getString(query.getColumnIndex(USER_ID))),
            query.getString(query.getColumnIndex(USER_NAME)),
            query.getInt(query.getColumnIndex(USER_ROLE))
        )
    }

    fun toContentValues(user: User): ContentValues {
        val contentValues = ContentValues()
        contentValues.put(USER_ID, user.id.toString())
        contentValues.put(USER_NAME, user.name)
        contentValues.put(USER_ROLE, user.role)
        return contentValues
    }

    const val USER_TABLE = "usr"
    const val USER_ID = "_id"
    const val USER_NAME = "name"
    const val USER_ROLE = "role"
}
