package com.example.class_booker.data.datasource.local.repo_iml

import com.example.class_booker.data.datasource.local.dao.RoomDao
import com.example.class_booker.data.model.Room
import kotlinx.coroutines.flow.Flow
import java.util.*

class LocalRoomDataSource(
    private val roomDao: RoomDao
) {
    suspend fun findAll(): List<Room> = roomDao.findAll()

    suspend fun findById(id: UUID): Room? = roomDao.findById(id)

    suspend fun insertAll(rooms: List<Room>) = roomDao.insertAll(rooms)
}