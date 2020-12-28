package com.example.class_booker.login.login

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.class_booker.data.ServiceLocator
import com.example.class_booker.data.datasource.local.dao.UserDao
import com.example.class_booker.login.data.LoginDataSource
import com.example.class_booker.login.data.LoginRepository

/**
 * ViewModel provider factory to instantiate LoginViewModel.
 * Required given LoginViewModel has a non-empty constructor
 */
class LoginViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(
                loginRepository = LoginRepository(
                    dataSource = LoginDataSource(UserDao(ServiceLocator.buildDBHelper(context)))
                )
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}