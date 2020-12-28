package com.example.class_booker.login.data

import com.example.class_booker.data.ServiceLocator
import com.example.class_booker.data.datasource.remote.UserService
import com.example.class_booker.data.model.User
import com.example.class_booker.login.data.model.LoggedInUser
import com.example.class_booker.utils.executeSafe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */

class LoginRepository(
    private val dataSource: LoginDataSource,
    private val remote: UserService = ServiceLocator.buildRetrofit(UserService::class.java)
) {
    suspend fun login(username: String): Result<LoggedInUser> = withContext(Dispatchers.IO) {
        var user = User(UUID.randomUUID(), username, 0)
        var isSuccess: Boolean

        val execute = remote.postUser(user).executeSafe()
        isSuccess = execute.isSuccessful
        execute.body()?.let { user = it }

        return@withContext if (isSuccess)
            dataSource.login(user)
        else
            Result.Error(message = execute.message())
    }
}