package com.example.class_booker.data

import android.content.Context
import com.example.class_booker.data.datasource.local.LocalDBHelper
import com.example.class_booker.data.datasource.remote.RetrofitClient
import okhttp3.OkHttpClient

const val BASE_URL = "http://192.168.0.105:8080"

object ServiceLocator {
    private val client = OkHttpClient.Builder().build()

    private var dbHelper: LocalDBHelper? = null

    fun <T> buildRetrofit(service: Class<T>): T {
        return RetrofitClient.getClient(BASE_URL).create(service)
    }

    fun buildDBHelper(context: Context?): LocalDBHelper {
        return dbHelper?.let {
            if (it.readableDatabase.isDatabaseIntegrityOk) {
                dbHelper = LocalDBHelper(context)
            }
            dbHelper
        } ?: LocalDBHelper(context)
    }
}