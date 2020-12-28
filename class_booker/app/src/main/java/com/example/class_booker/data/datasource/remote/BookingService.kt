package com.example.class_booker.data.datasource.remote

import com.example.class_booker.data.UserHolder
import com.example.class_booker.data.model.Booking
import retrofit2.Call
import retrofit2.http.*
import java.util.*

interface BookingService {

    @GET("booking/user")
    fun getBookingByUser(@Query("from") from: String, @Query("to") to: String, @Query("userId") userId: UUID = UserHolder.id!!):
            Call<List<Booking>>

    @GET("booking/room")
    fun getBookingByRoom(@Query("from") from: String, @Query("to") to: String, @Query("roomId") roomId: UUID): Call<List<Booking>>

    @GET("booking/user/{id}")
    fun getAllBookingsByUser(@Path("id") id: UUID = UserHolder.id!!): Call<List<Booking>>

    @GET("booking/{id}")
    fun getBookingById(@Path("id") id: UUID): Call<Booking>

    @POST("booking")
    fun postBooking(@Body booking: Booking): Call<Booking>

    @DELETE("booking/{id}")
    fun deleteBooking(@Path("id") id: UUID): Call<Nothing>

    @POST("booking/all")
    fun uploadBookings(@Body bookings: List<Booking>): Call<List<Booking>>
}
