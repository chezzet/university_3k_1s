package com.example.class_booker.data.datasource.remote

import com.example.class_booker.data.model.User
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface UserService {

    @POST("user")
    fun postUser(@Body user: User): Call<User>

    @GET("user")
    fun getUsers(): Call<List<User>>
}