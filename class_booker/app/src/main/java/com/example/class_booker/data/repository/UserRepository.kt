package com.example.class_booker.data.repository

import android.util.Log
import com.example.class_booker.data.ServiceLocator
import com.example.class_booker.data.UserHolder
import com.example.class_booker.data.datasource.local.repo_iml.LocalUserDataSource
import com.example.class_booker.data.datasource.remote.UserService
import com.example.class_booker.data.model.User
import com.example.class_booker.utils.executeSafe
import java.util.*

class UserRepository(
    private val localUserDataSource: LocalUserDataSource,
    private val remote: UserService = ServiceLocator.buildRetrofit(UserService::class.java)
) {

    suspend fun findById(id: UUID): User? {
        return localUserDataSource.findById(id)
    }

    suspend fun save(user: User) {
        localUserDataSource.save(user)
    }

    suspend fun updateCurrentUser(id: UUID) {
        findById(id)?.let {
            UserHolder.id = it.id
            UserHolder.name = it.name
            UserHolder.role = it.role
        }
    }

    suspend fun sync() {
        val executeSafe = remote.getUsers().executeSafe()
        if (executeSafe.isSuccessful) {
            executeSafe.body()?.let { list ->
                list.forEach {
                    kotlin.runCatching {
                        localUserDataSource.save(it)
                    }
                }
            }
        } else {
            Log.w(RoomRepository::class.java.simpleName, "Cant fetch users info")
        }
    }
}