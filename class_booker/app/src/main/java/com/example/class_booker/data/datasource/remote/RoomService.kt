package com.example.class_booker.data.datasource.remote

import com.example.class_booker.data.model.Room
import retrofit2.Call
import retrofit2.http.GET

interface RoomService {

    @GET("room")
    fun getRooms(): Call<List<Room>>
}