package com.example.class_booker.data.datasource.local

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.class_booker.data.model.BookingCompanion.BOOKING_DELETED
import com.example.class_booker.data.model.BookingCompanion.BOOKING_FROM_TIME
import com.example.class_booker.data.model.BookingCompanion.BOOKING_ID
import com.example.class_booker.data.model.BookingCompanion.BOOKING_ROOM_ID
import com.example.class_booker.data.model.BookingCompanion.BOOKING_TABLE
import com.example.class_booker.data.model.BookingCompanion.BOOKING_TIMESTAMP
import com.example.class_booker.data.model.BookingCompanion.BOOKING_TITLE
import com.example.class_booker.data.model.BookingCompanion.BOOKING_TO_TIME
import com.example.class_booker.data.model.BookingCompanion.BOOKING_UPLOADED
import com.example.class_booker.data.model.BookingCompanion.BOOKING_USER_ID
import com.example.class_booker.data.model.RoomCompanion.ROOM_CAPACITY
import com.example.class_booker.data.model.RoomCompanion.ROOM_DELETED
import com.example.class_booker.data.model.RoomCompanion.ROOM_DESCRIPTION
import com.example.class_booker.data.model.RoomCompanion.ROOM_ID
import com.example.class_booker.data.model.RoomCompanion.ROOM_NAME
import com.example.class_booker.data.model.RoomCompanion.ROOM_TABLE
import com.example.class_booker.data.model.RoomCompanion.ROOM_TIMESTAMP
import com.example.class_booker.data.model.UserCompanion.USER_ID
import com.example.class_booker.data.model.UserCompanion.USER_NAME
import com.example.class_booker.data.model.UserCompanion.USER_ROLE
import com.example.class_booker.data.model.UserCompanion.USER_TABLE

const val LOCAL_DB_NAME = "local_data"

class LocalDBHelper(context: Context?) : SQLiteOpenHelper(context, LOCAL_DB_NAME, null, 1) {
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(
            "CREATE TABLE $USER_TABLE (" +
                    "$USER_ID text PRIMARY KEY, " +
                    "$USER_NAME text, " +
                    "$USER_ROLE byte" +
                    ")"
        )
        db?.execSQL(
            "CREATE TABLE $ROOM_TABLE (" +
                    "$ROOM_ID text PRIMARY KEY, " +
                    "$ROOM_NAME text, " +
                    "$ROOM_DESCRIPTION text, " +
                    "$ROOM_CAPACITY text, " +
                    "$ROOM_DELETED boolean DEFAULT 0, " +
                    "$ROOM_TIMESTAMP timestamp" +
                    ")"
        )
        db?.execSQL(
            "CREATE TABLE $BOOKING_TABLE (" +
                    "$BOOKING_ID text PRIMARY KEY, " +
                    "$BOOKING_TITLE text, " +
                    "$BOOKING_ROOM_ID text REFERENCES $ROOM_TABLE($ROOM_ID) ON DELETE CASCADE, " +
                    "$BOOKING_USER_ID text REFERENCES $USER_TABLE($USER_ID) ON DELETE CASCADE DEFAULT NULL, " +
                    "$BOOKING_FROM_TIME timestamp, " +
                    "$BOOKING_TO_TIME timestamp, " +
                    "$BOOKING_TIMESTAMP timestamp, " +
                    "$BOOKING_DELETED boolean DEFAULT 0, " +
                    "$BOOKING_UPLOADED boolean DEFAULT 0" +
                    ")"
        )

        db?.execSQL(
            "CREATE TRIGGER IF NOT EXISTS ${BOOKING_TABLE}_after_insert AFTER INSERT ON $BOOKING_TABLE " +
                    "WHEN NEW.$BOOKING_UPLOADED = 1 AND NEW.$BOOKING_DELETED = 1 " +
                    "BEGIN " +
                    "   DELETE FROM $BOOKING_TABLE WHERE $BOOKING_ID = NEW.$BOOKING_ID;" +
                    "END;"
        )
        db?.execSQL(
            "CREATE TRIGGER IF NOT EXISTS ${BOOKING_TABLE}_after_insert_time BEFORE UPDATE ON $BOOKING_TABLE " +
                    "WHEN NEW.$BOOKING_TIMESTAMP < OLD.$BOOKING_TIMESTAMP " +
                    "BEGIN " +
                    "   SELECT RAISE(IGNORE);" +
                    "END;"
        )
        db?.execSQL(
            "CREATE TRIGGER IF NOT EXISTS ${BOOKING_TABLE}_after_update AFTER UPDATE ON $BOOKING_TABLE " +
                    "WHEN NEW.$BOOKING_UPLOADED = 1 AND NEW.$BOOKING_DELETED = 1 " +
                    "BEGIN " +
                    "   DELETE FROM $BOOKING_TABLE WHERE $BOOKING_ID = NEW.$BOOKING_ID;" +
                    "END;"
        )
        db?.execSQL(
            "CREATE TRIGGER IF NOT EXISTS ${ROOM_TABLE}_after_insert AFTER INSERT ON $ROOM_TABLE " +
                    "WHEN NEW.$ROOM_DELETED = 1 " +
                    "BEGIN " +
                    "   DELETE FROM $ROOM_TABLE WHERE $ROOM_ID = NEW.$ROOM_ID;" +
                    "END;"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    }

    override fun onConfigure(db: SQLiteDatabase?) {
        db?.setForeignKeyConstraintsEnabled(true)
        super.onConfigure(db)
    }
}