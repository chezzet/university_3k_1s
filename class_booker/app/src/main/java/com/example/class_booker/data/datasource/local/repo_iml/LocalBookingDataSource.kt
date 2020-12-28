package com.example.class_booker.data.datasource.local.repo_iml

import com.example.class_booker.data.UserHolder
import com.example.class_booker.data.datasource.local.dao.BookingDao
import com.example.class_booker.data.model.Booking
import java.util.*

class LocalBookingDataSource(
    private val bookingDao: BookingDao
) {
    suspend fun findAllBetween(from: Date, to: Date): List<Booking> = bookingDao.findAllBetween(from, to)

    suspend fun findAllBetween(from: Date, to: Date, roomId: UUID): List<Booking> = bookingDao.findAllBetween(from, to, roomId)

    suspend fun findById(id: UUID): Booking? = bookingDao.findById(id)

    suspend fun checkAvailability(from: Date, to: Date, roomId: UUID): Boolean {
        return bookingDao.findAllBetween(from, to, roomId).count() == 0
    }

    suspend fun save(booking: Booking) = bookingDao.save(booking)

    suspend fun update(booking: Booking) = bookingDao.update(booking)

    suspend fun insertAll(bookings: List<Booking>) = bookingDao.insertAll(bookings)

    suspend fun findAllByUserNotUploaded(userId: UUID = UserHolder.id!!) = bookingDao.findAllByUserNotUploaded(userId)
}