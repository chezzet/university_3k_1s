package com.example.class_booker.data.datasource.local.repo_iml

import com.example.class_booker.data.datasource.local.dao.UserDao
import com.example.class_booker.data.model.User
import java.util.*

class LocalUserDataSource(
    private val userDao: UserDao
) {

    suspend fun findById(id: UUID): User? {
        return userDao.findById(id)
    }

    suspend fun save(user: User) {
        userDao.save(user)
    }
}