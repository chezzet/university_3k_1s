package com.example.class_booker.data.repository

import android.util.Log
import com.example.class_booker.data.ServiceLocator
import com.example.class_booker.data.datasource.local.repo_iml.LocalBookingDataSource
import com.example.class_booker.data.datasource.remote.BookingService
import com.example.class_booker.data.model.Booking
import com.example.class_booker.utils.executeSafe
import com.example.class_booker.utils.toUTC
import com.example.class_booker.utils.toUTCString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class BookingRepository(
    private val localDataSource: LocalBookingDataSource,
    private val remoteRepository: BookingService = ServiceLocator.buildRetrofit(BookingService::class.java)
) {

    fun findAllBetween(from: Date, to: Date): Flow<List<Booking>> = flow {
        val fromUTC = from.toUTC()
        val toUTC = to.toUTC()

        val result = localDataSource.findAllBetween(fromUTC, toUTC)
        emit(result)
        run {
            val execute = remoteRepository.getBookingByUser(from.toUTCString(), to.toUTCString()).executeSafe()
            if (execute.isSuccessful) {
                execute.body()?.let {
                    localDataSource.insertAll(it)
                    emit(localDataSource.findAllBetween(fromUTC, toUTC))
                }
            }
        }
    }.flowOn(Dispatchers.IO)

    fun findAllBetween(from: Date, to: Date, roomId: UUID): Flow<List<Booking>> = flow {
        val local = localDataSource.findAllBetween(from.toUTC(), to.toUTC(), roomId)
        emit(local)
        run {
            val execute = remoteRepository.getBookingByRoom(from.toUTCString(), to.toUTCString(), roomId).executeSafe()
            if (execute.isSuccessful) {
                execute.body()?.let {
                    localDataSource.insertAll(it)
                    emit(localDataSource.findAllBetween(from.toUTC(), to.toUTC(), roomId))
                }
            }
        }
    }.flowOn(Dispatchers.IO)

    suspend fun findById(id: UUID): Booking? = localDataSource.findById(id)

    suspend fun checkAvailability(from: Date, to: Date, roomId: UUID): Boolean = withContext(Dispatchers.IO) {
        val local = localDataSource.checkAvailability(from.toUTC(), to.toUTC(), roomId)
        var remote: Boolean = true
        val bookingByRoom = remoteRepository.getBookingByRoom(from.toUTCString(), to.toUTCString(), roomId).executeSafe()
        if (bookingByRoom.isSuccessful) {
            remote = bookingByRoom.body()?.none { !it.deleted } ?: true
            if (remote != local) {
                async {
                    bookingByRoom.body()?.let { localDataSource.insertAll(it) }
                }
            }
        }
        return@withContext local && remote
    }

    suspend fun save(booking: Booking) {
        localDataSource.save(booking)
        remoteRepository.postBooking(booking).enqueue(object : Callback<Booking> {
            override fun onResponse(call: Call<Booking>, response: Response<Booking>) {
                response.body()?.let {
                    runBlocking {
                        localDataSource.update(booking)
                    }
                }
            }

            override fun onFailure(call: Call<Booking>, t: Throwable) {
                Log.i(BookingRepository::class.simpleName, "Error on post booking", t)
            }
        })
    }

    suspend fun update(booking: Booking) {
        localDataSource.update(booking)
        remoteRepository.postBooking(booking).enqueue(object : Callback<Booking> {
            override fun onResponse(call: Call<Booking>, response: Response<Booking>) {
                response.body()?.let {
                    runBlocking {
                        localDataSource.update(booking)
                    }
                }
            }

            override fun onFailure(call: Call<Booking>, t: Throwable) {
                Log.i(BookingRepository::class.simpleName, "Error on post booking", t)
            }
        })
    }

    suspend fun sync() {
        val notUploaded = localDataSource.findAllByUserNotUploaded()
        val uploaded = remoteRepository.uploadBookings(notUploaded).executeSafe()
        if (uploaded.isSuccessful)
            uploaded.body()?.let {
                localDataSource.insertAll(it)
            }
        else
            Log.i(BookingRepository::class.simpleName, "Can't fetch bookings info")

        val fetched = remoteRepository.getAllBookingsByUser().executeSafe()
        if (fetched.isSuccessful) {
            fetched.body()?.let {
                localDataSource.insertAll(it)
            }
        } else {
            Log.i(BookingRepository::class.simpleName, "Can't fetch bookings info")
        }
    }
}



