package com.example.bd_project.repository

import com.example.bd_project.model.Booking
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface BookingRepository : JpaRepository<Booking, UUID> {

    @Query("FROM Booking WHERE fromTime BETWEEN ?1 AND ?2 OR toTime BETWEEN ?1 AND ?2 AND userId = ?3")
    fun findAllByFromTimeBetweenOrAndToTimeBetweenByUser(start: Date, end: Date, userId: UUID): List<Booking>

    @Query("FROM Booking WHERE fromTime BETWEEN ?1 AND ?2 OR toTime BETWEEN ?1 AND ?2 AND roomId = ?3")
    fun findAllByFromTimeBetweenOrAndToTimeBetweenByRoom(start: Date, end: Date, roomId: UUID): List<Booking>
    
    fun findAllByUserId(userId: UUID): List<Booking>
}