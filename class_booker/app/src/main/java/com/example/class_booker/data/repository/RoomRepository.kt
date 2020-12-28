package com.example.class_booker.data.repository

import android.util.Log
import com.example.class_booker.data.ServiceLocator
import com.example.class_booker.data.datasource.local.repo_iml.LocalRoomDataSource
import com.example.class_booker.data.datasource.remote.RoomService
import com.example.class_booker.data.model.Room
import com.example.class_booker.utils.executeSafe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import java.util.*

class RoomRepository(
    private val localDataSource: LocalRoomDataSource,
    private val roomService: RoomService = ServiceLocator.buildRetrofit(RoomService::class.java)
) {

    fun findAll(): Flow<List<Room>> = flow {
        val localResult = localDataSource.findAll()
        emit(localResult)
        run {
            val execute = roomService.getRooms().executeSafe()
            if (execute.isSuccessful) {
                execute.body()?.let {
                    emit(it)
                    localDataSource.insertAll(it)
                }
            }
        }
    }.flowOn(Dispatchers.IO)

    suspend fun findById(id: UUID): Flow<Room> =
        localDataSource.findById(id)?.let { flowOf(it) } ?: emptyFlow()

    suspend fun sync() {
        val executeSafe = roomService.getRooms().executeSafe()
        if (executeSafe.isSuccessful) {
            executeSafe.body()?.let {
                localDataSource.insertAll(it)
            }
        } else {
            Log.w(RoomRepository::class.java.simpleName, "Cant fetch rooms info")
        }
    }
}