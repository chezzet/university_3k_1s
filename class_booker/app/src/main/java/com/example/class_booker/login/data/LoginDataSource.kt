package com.example.class_booker.login.data

import com.example.class_booker.data.UserHolder
import com.example.class_booker.data.datasource.local.dao.UserDao
import com.example.class_booker.data.model.User
import com.example.class_booker.login.data.model.LoggedInUser
import java.io.IOException

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class LoginDataSource(
    private val userDao: UserDao
) {

    suspend fun login(user: User): Result<LoggedInUser> {
        try {
            val check = userDao.findByName(user.name)
            check?.let {
                UserHolder.id = it.id
                UserHolder.name = it.name
                UserHolder.role = it.role
                return Result.Success(LoggedInUser(it.id.toString(), it.name))
            }
            userDao.save(user)

            UserHolder.id = user.id
            UserHolder.name = user.name
            UserHolder.role = user.role

            return Result.Success(LoggedInUser(user.id.toString(), user.name))

        } catch (e: Throwable) {
            return Result.Error(IOException("Error logging in", e))
        }
    }

    fun logout() {

    }
}